#### function declerations - start

print(paste0("Start: ", Sys.time()))

standardize <- function(field, value)
{
  return ((value - trainingDataMean[field]) / trainingDataSd[field])
}

destandardize <- function(field, value)
{
  return ((value * trainingDataSd[field]) + trainingDataMean[field])
}

sigmoid <- function(n)
{
  sig <- 1 / (1 + exp(-n))
  return (sig)
}

applyCCoding <- function(dataSet, numericSet)
{

  monthData = data.frame(dataSet$month)
  dayData = data.frame(dataSet$day)
  
  monthCoded = model.matrix(~ . + 0, data=monthData, contrasts.arg = lapply(monthData, contrasts, contrasts=FALSE))
  dayCoded = model.matrix(~ . + 0, data=dayData, contrasts.arg = lapply(dayData, contrasts, contrasts=FALSE))
  
  monthCoded = data.frame(monthCoded)
  dayCoded = data.frame(dayCoded)
  
  dataSet = data.frame(numericSet, 
                            monthCoded$dataSet.monthjan, monthCoded$dataSet.monthfeb,
                            monthCoded$dataSet.monthmar, monthCoded$dataSet.monthapr,
                            monthCoded$dataSet.monthmay, monthCoded$dataSet.monthjun,
                            monthCoded$dataSet.monthjul, monthCoded$dataSet.monthaug,
                            monthCoded$dataSet.monthsep, monthCoded$dataSet.monthoct,
                            monthCoded$dataSet.monthdec,
                            dayCoded$dataSet.daymon, dayCoded$dataSet.daytue,
                            dayCoded$dataSet.daywed, dayCoded$dataSet.daythu,
                            dayCoded$dataSet.dayfri, dayCoded$dataSet.daysat,
                            dayCoded$dataSet.daysun)
  
  colnames(dataSet) <- c("X","Y", "FFMC", "DMC", "DC", "ISI", "temp", "RH", "wind", "rain", "area", 
                              "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "dec",
                              "mon", "tue", "wed", "thu", "fri", "sat", "sun")
  return (dataSet)
}


#### read data

trainingData = read.csv(file="forestfires_data_tr.csv", header=TRUE, sep=";")
validationData = read.csv(file="forestfires_data_val.csv", header=TRUE, sep=";")

## prepare data

trainingDataNumeric = data.frame(trainingData[c('X','Y','FFMC','DMC','DC','ISI','temp','RH','wind','rain','area')])
validationDataNumeric = data.frame(validationData[c('X','Y','FFMC','DMC','DC','ISI','temp','RH','wind','rain','area')])

trainingDataNumeric <- data.frame(lapply(trainingDataNumeric, function(x) {
  as.double(sub(",", ".", x, fixed = TRUE))
}))

validationDataNumeric <- data.frame(lapply(validationDataNumeric, function(x) {
  as.double(sub(",", ".", x, fixed = TRUE))
}))

trainingDataMean = colMeans(trainingDataNumeric)
validationDataMean = colMeans(validationDataNumeric)

trainingDataSd = sapply(trainingDataNumeric, sd)
validationDataSd = sapply(validationDataNumeric, sd)

trainingDataNumeric = data.frame(scale(trainingDataNumeric))
validationDataNumeric = data.frame(scale(validationDataNumeric))

## make data suitable for multi layer perceptron with 1-of-C coding and standardization

trainingSet = applyCCoding(trainingData, trainingDataNumeric)
validationSet = applyCCoding(validationData, validationDataNumeric)

trainingSet = cbind(bias = 1, trainingSet)
validationSet = cbind(bias = 1, validationSet)

trainingOutputSet = data.frame(trainingSet$area)
validationOutputSet = data.frame(validationSet$area)

drops <- c("area")
trainingSet = trainingSet[ , !(names(trainingSet) %in% drops)]
validationSet = validationSet[ , !(names(validationSet) %in% drops)]

## hyper parameters
numberOfOutputNodes = 1

minHiddenNeurons = 1
maxHiddenNeurons = 10

epoch = 500
lerningRateDefault = 0.2
epochLogNumber = 10


errorMatrix = matrix(0, ncol = 4, nrow = ((epoch / epochLogNumber) * (maxHiddenNeurons - minHiddenNeurons + 1)))
errorSet = data.frame(errorMatrix)
colnames(errorSet)<-c("H","epoch","trainingMSE", "validationSME")

for (hiddenNeuronNumber in minHiddenNeurons:maxHiddenNeurons) {
  learningRate = lerningRateDefault
  numberOfHiddenUnits = hiddenNeuronNumber;
  
  ## weight initialization

  inputWeight = matrix(sample(c(-100:100), numberOfHiddenUnits * (dim(trainingSet)[2]), TRUE)/10000, 
                       numberOfHiddenUnits, (dim(trainingSet)[2]))
  
  hiddenWeight = matrix(sample(c(-100:100), numberOfOutputNodes * (numberOfHiddenUnits + 1), TRUE)/10000, 
                        numberOfOutputNodes, (numberOfHiddenUnits + 1))
  
  hiddenValues =  matrix(sample(c(0:0), 1 * (numberOfHiddenUnits + 1), TRUE), 
                         1, (numberOfHiddenUnits + 1))
  
  hiddenValues[,1] = 1 ## hidden bias unit
  
  print(paste0("Start - Epoch: ", Sys.time()))
  
  
  ## run epochs
  for (e in 1:epoch){
    for(x in 1:dim(trainingSet)[1])  # train set for each row
    {
          for (h in 2:dim(hiddenValues)[2]) {
              hiddenSum = sum(inputWeight[h-1,] * trainingSet[x,])
              zh = sigmoid(hiddenSum)
              hiddenValues[1,h] = zh
          }
      
          y = sum(hiddenWeight[1,] * hiddenValues[1,])
          inputError = trainingOutputSet[x,1] - y
          deltaHidden = learningRate*inputError*hiddenValues
          
          for (h in 2:dim(hiddenValues)[2]) {
            for (j in 1:dim(trainingSet)[2]) {
                deltaInput = inputError * hiddenWeight[1,h] * hiddenValues[1,h] * (1 - hiddenValues[1,h]) * trainingSet[x,j]
                inputWeight[h-1,j] = inputWeight[h-1,j] + deltaInput
            }
          }
          
          hiddenWeight = hiddenWeight + deltaHidden
    }
    
    if (e %% epochLogNumber == 0) {
      hiddenValuesTr =  matrix(sample(c(0:0), 1 * (numberOfHiddenUnits + 1), TRUE), 
                             1, (numberOfHiddenUnits + 1))
      
      hiddenValuesTr[,1] = 1 ## hidden bias unit
      
      trainingErrorSum = 0
      
      # calculate training error
      for(x in 1:dim(trainingSet)[1])
      {
          for (h in 2:dim(hiddenValuesTr)[2]) {
            hiddenSum = sum(inputWeight[h-1,] * trainingSet[x,])
            zh = sigmoid(hiddenSum)
            hiddenValuesTr[1,h] = zh
          }
          
          y = sum(hiddenWeight[1,] * hiddenValuesTr[1,])
          trainingErrorSum = trainingErrorSum + ((trainingOutputSet[x,1] - y)^2)
      }
      
      # training error - mean square error
      trainingMSE = trainingErrorSum / dim(trainingSet)[1]
      
      hiddenValuesVal =  matrix(sample(c(0:0), 1 * (numberOfHiddenUnits + 1), TRUE), 
                               1, (numberOfHiddenUnits + 1))
      
      hiddenValuesVal[,1] = 1 ## hidden bias unit
      
      validationErrorSum = 0
      
      # calculate validation error
      for(x in 1:dim(validationSet)[1])
      {
          for (h in 2:dim(hiddenValuesVal)[2]) {
            hiddenSum = sum(inputWeight[h-1,] * validationSet[x,])
            zh = sigmoid(hiddenSum)
            hiddenValuesVal[1,h] = zh
          }
          
          y = sum(hiddenWeight[1,] * hiddenValuesVal[1,])
          validationErrorSum = validationErrorSum + ((validationOutputSet[x,1] - y)^2) 
      }
      
      # validation error - mean square error
      validationMSE = validationErrorSum / dim(validationSet)[1]
      
      epochLogIndex = ((epoch / epochLogNumber) * (hiddenNeuronNumber - 1)) + (e / epochLogNumber)
      errorSet[epochLogIndex,1] = hiddenNeuronNumber
      errorSet[epochLogIndex,2] = e
      errorSet[epochLogIndex,3] = trainingMSE
      errorSet[epochLogIndex,4] = validationMSE
    }
    learningRate = learningRate * 0.998
    print(paste0("H: ", hiddenNeuronNumber, ", epoch:", e))
  }
}

paste0("ANN Finish: ", Sys.time())

paste0("Plot Start: ", Sys.time())

for (p in minHiddenNeurons:maxHiddenNeurons) {
  subErrorSet = errorSet[errorSet$H == p, ]
  subTrainingError = data.frame(subErrorSet$epoch, subErrorSet$trainingMSE)
  subValidationError = data.frame(subErrorSet$epoch, subErrorSet$validationSME)
  
  plot(subTrainingError,type="l",col=4, lty = 2, xlab="Epoch", ylab="Mean Square Error")
  #axis(1, xaxp=c(epochLogNumber*5, epoch, (epoch/(epochLogNumber*5)) - 1), las=2)
  lines(subValidationError,col=6,lty = 1, pch = 4)
  title(paste0("Number of Hidden Neurons: ", p))
  legend("topright", c("Training", "Validation"), col = c(4, 6),
         text.col = "black", lty = c(2,1),
         bg = "gray90", cex = 0.5)

}
