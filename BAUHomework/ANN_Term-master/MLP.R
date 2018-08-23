MLP <- function(trainingData, validationData, learningRate, numberOfHiddenNeurons, 
                epoch, epochLogNumber, targetClass, numberOfOutputNodes, writeResult,filePrefix)
{
  # prepare data
  
  print("MLP Start:")
  print(date())
  
  targetCols <- c(targetClass)
  
  trainingSet = trainingData[ , !(names(trainingData) %in% targetCols)]
  validationSet = validationData[ , !(names(validationData) %in% targetCols)]
  
  trainingSet = cbind(bias = 1, trainingSet)
  validationSet = cbind(bias = 1, validationSet)
  
  trainingOutputSet = data.frame(trainingData[ , (names(trainingData) %in% targetCols)])
  validationOutputSet = data.frame(validationData[ , (names(validationData) %in% targetCols)])
  
  
  # create error matrix
  errorMatrix = matrix(0, ncol = 14, nrow = ((epoch / epochLogNumber)))
  errorSet = data.frame(errorMatrix)
  colnames(errorSet)<-c("H","epoch","Train-TP", "Train-FP", "Train-TN", "Train-FN", "Train-%", "Train-CE",
                        "Val-TP", "Val-FP", "Val-TN", "Val-FN", "Val-%", "Val-CE")
  
  ## weight initialization
  
  inputWeight = matrix(sample(c(-100:100), numberOfHiddenNeurons * (dim(trainingSet)[2]), TRUE)/10000, 
                       numberOfHiddenNeurons, (dim(trainingSet)[2]))
  
  hiddenWeight = matrix(sample(c(-100:100), numberOfOutputNodes * (numberOfHiddenNeurons + 1), TRUE)/10000, 
                        numberOfOutputNodes, (numberOfHiddenNeurons + 1))
  
  hiddenValues =  matrix(sample(c(0:0), 1 * (numberOfHiddenNeurons + 1), TRUE), 
                         1, (numberOfHiddenNeurons + 1))
  
  hiddenValues[,1] = 1 ## hidden bias unit
  
  ## run epochs
  for (e in 1:epoch){
    for(x in 1:dim(trainingSet)[1])  # train set for each row
    {
      for (h in 2:dim(hiddenValues)[2]) {
        hiddenSum = sum(inputWeight[h-1,] * trainingSet[x,])
        zh = sigmoid(hiddenSum)
        hiddenValues[1,h] = zh
      }
      
      y = sigmoid(sum(hiddenWeight[1,] * hiddenValues[1,]))
      
      foundClass = 0
      if (y > 0.5) {
        foundClass = 1
      }
      
      inputError = trainingOutputSet[x,1]*log(y) + (1-trainingOutputSet[x,1]) * log(1-y)
      deltaHidden = learningRate*(trainingOutputSet[x,1] - y)*hiddenValues
      
      for (h in 2:dim(hiddenValues)[2]) {
        for (j in 1:dim(trainingSet)[2]) {
          deltaInput = (trainingOutputSet[x,1] - y) * hiddenWeight[1,h] * hiddenValues[1,h] * (1 - hiddenValues[1,h]) * trainingSet[x,j]
          inputWeight[h-1,j] = inputWeight[h-1,j] + deltaInput
        }
      }
      
      hiddenWeight = hiddenWeight + deltaHidden
    }
    
    if (e %% epochLogNumber == 0) {
      trainingErrorSum = 0
      validationErrorSum = 0
      
      trainTP = 0
      trainFP = 0
      trainTN = 0
      trainFN = 0
      
      valTP = 0
      valFP = 0
      valTN = 0
      valFN = 0
      
      hiddenValuesTr =  matrix(sample(c(0:0), 1 * (numberOfHiddenNeurons + 1), TRUE), 
                               1, (numberOfHiddenNeurons + 1))
      
      hiddenValuesTr[,1] = 1 ## hidden bias unit
      
      # calculate training error
      for(x in 1:dim(trainingSet)[1])
      {
        for (h in 2:dim(hiddenValuesTr)[2]) {
          hiddenSum = sum(inputWeight[h-1,] * trainingSet[x,])
          zh = sigmoid(hiddenSum)
          hiddenValuesTr[1,h] = zh
        }
        
        y = sigmoid(sum(hiddenWeight[1,] * hiddenValuesTr[1,]))
        r = trainingOutputSet[x,1]
        trainingErrorSum = trainingErrorSum + (-(r*log(y) + (1-r) * log(1 - y)))
        
        foundClass = 0
        if (y > 0.5) {
          foundClass = 1
        }
        
        if (foundClass == trainingOutputSet[x,1] && foundClass == 1) {
          trainTP = trainTP + 1
        } else if (foundClass == trainingOutputSet[x,1] && foundClass == 0) {
          trainTN = trainTN + 1
        } else if (foundClass != trainingOutputSet[x,1] && foundClass == 0) {
          trainFN = trainFN + 1
        } else if (foundClass != trainingOutputSet[x,1] && foundClass == 1) {
          trainFP = trainFP + 1
        }
      }
      
      hiddenValuesVal =  matrix(sample(c(0:0), 1 * (numberOfHiddenNeurons + 1), TRUE), 
                                1, (numberOfHiddenNeurons + 1))
      
      hiddenValuesVal[,1] = 1 ## hidden bias unit
      
      # calculate validation error
      for(x in 1:dim(validationSet)[1])
      {
        for (h in 2:dim(hiddenValuesVal)[2]) {
          hiddenSum = sum(inputWeight[h-1,] * validationSet[x,])
          zh = sigmoid(hiddenSum)
          hiddenValuesVal[1,h] = zh
        }
        
        y = sigmoid(sum(hiddenWeight[1,] * hiddenValuesVal[1,]))
        r = validationOutputSet[x,1] 
        
        validationErrorSum = validationErrorSum + (-(r*log(y) + (1-r) * log(1 - y)))
        
        foundClass = 0
        if (y > 0.5) {
          foundClass = 1
        }
        
        if (foundClass == validationOutputSet[x,1] && foundClass == 1) {
          valTP = valTP + 1
        } else if (foundClass == validationOutputSet[x,1] && foundClass == 0) {
          valTN = valTN + 1
        } else if (foundClass != validationOutputSet[x,1] && foundClass == 0) {
          valFN = valFN + 1
        } else if (foundClass != validationOutputSet[x,1] && foundClass == 1) {
          valFP = valFP + 1
        }
      }
      
      epochLogIndex = (e / epochLogNumber) #((epoch / epochLogNumber) * (hiddenNeuronNumber - 1)) + (e / epochLogNumber)
      errorSet[epochLogIndex,1] = numberOfHiddenNeurons
      errorSet[epochLogIndex,2] = e
      errorSet[epochLogIndex,3] = trainTP
      errorSet[epochLogIndex,4] = trainFP
      errorSet[epochLogIndex,5] = trainTN
      errorSet[epochLogIndex,6] = trainFN
      errorSet[epochLogIndex,7] = (trainFN + trainFP) / (trainTN + trainTP + trainFN + trainFP)
      errorSet[epochLogIndex,8] = trainingErrorSum
      errorSet[epochLogIndex,9] = valTP
      errorSet[epochLogIndex,10] = valFP
      errorSet[epochLogIndex,11] = valTN
      errorSet[epochLogIndex,12] = valFN
      errorSet[epochLogIndex,13] = (valFN + valFP) / (valTN + valTP + valFN + valFP)
      errorSet[epochLogIndex,14] = validationErrorSum
    }
    
    learningRate = learningRate * 0.998
    print(paste0("H: ", numberOfHiddenNeurons, ", epoch:", e))
  }
  
  print("MLP End:")
  print(date())
  
  result <- list("Error" = errorSet, "InputWeight" = inputWeight, "HiddenWeight" = hiddenWeight)
  
  if (writeResult == 1) {
    errorFileName = paste0(filePrefix,"_Error_H_", numberOfHiddenNeurons, "_Epoch_", e, ".csv")
    write.csv(errorSet, errorFileName)
    
    iwFileName = paste0(filePrefix,"_IW_H_", numberOfHiddenNeurons, "_Epoch_", e, ".csv")
    write.csv(inputWeight, iwFileName)
    
    hwFileName = paste0(filePrefix,"_HW_H_", numberOfHiddenNeurons, "_Epoch_", e, ".csv")
    write.csv(hiddenWeight, hwFileName)
    
  }
  
  return (result)
}

sigmoid <- function(n)
{
  sig <- 1 / (1 + exp(-n))
  return (sig)
}