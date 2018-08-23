source("functions.R")
source("MLP.R")
source("RBF.R")
source("KNN.R")
setwd("C:/Users/sametyazak/Desktop/ynwa/bau/2017 - Spring/ann/term project/")

## prepare data
allstarPlayersFull = read.csv(file="data/allstar_2000_2017.csv", header=TRUE, sep=";")
allstarPlayers = data.frame(allstarPlayersFull$Starters, allstarPlayersFull$Year, allstarPlayersFull$Team)
colnames(allstarPlayers) <- c("Player", "Year", "Conference")

allYearStatsTr = data.frame()
allYearStatsVal = data.frame()

yearStart = 1999
yearEnd = 2017

balanceType = 2 # 0: no balance, 1: duplicate values, 2: delete majority class

counter = 0

for (i in yearStart:(yearEnd-1))
{
  currentYearPath = paste0("data/", i, "_", i+1, ".csv")
  currentYearStats = read.csv(file=currentYearPath, header=TRUE, sep=";")
  currentYearStats = cbind(currentYearStats, SeasonStart = i, SeasonEnd = i+1, IsAllStar = 0)
  
  combinedStats = merge(x = currentYearStats, y = allstarPlayers, 
                        by.x = c("Player", "SeasonEnd"), by.y = c("Player", "Year"),
                        all.x = TRUE)
  
  combinedStats <- within(combinedStats, IsAllStar[!is.na(Conference)] <- 1)
  counter = counter + 1
  
  if (counter %% 2 == 0) {
    if (length(allYearStatsVal) == 0) {
      allYearStatsVal = combinedStats
    }
    else {
      allYearStatsVal = rbind(allYearStatsVal, combinedStats)
    }
  } else {
    if (length(allYearStatsTr) == 0) {
      allYearStatsTr = combinedStats
    }
    else {
      allYearStatsTr = rbind(allYearStatsTr, combinedStats)
    }  
  }
  
}

# drop non numeric columns
trainingSet = getNumericalStats(allYearStatsTr)
validationSet = getNumericalStats(allYearStatsVal)

# calculate correlation
corrMatrix = calculateCorrelation(trainingSet, allYearStatsTr$IsAllStar)

## balance class if needed

if (balanceType == 1) {
  allYearStatsBalanced = duplicateLowerData(allYearStatsTr, 0.4)
  trainingSetBalanced = getNumericalStats(allYearStatsBalanced)
} else if (balanceType == 2) {
  trainAllStarPlayers = allYearStatsTr[allYearStatsTr$IsAllStar == 1, ]
  trainNonAllStarPlayers = allYearStatsTr[allYearStatsTr$IsAllStar == 0, ]
  
  allYearStatsBalanced = trainAllStarPlayers
  nonAllStarCount = dim(trainNonAllStarPlayers)[1]
  
  for (i in 1:dim(trainAllStarPlayers)[1]) {
    randomPlayerIndex =sample(1:nonAllStarCount, 1)
    
    allYearStatsBalanced = rbind(allYearStatsBalanced, trainNonAllStarPlayers[randomPlayerIndex,])
  }
  
  trainingSetBalanced = getNumericalStats(allYearStatsBalanced)
} else {
  allYearStatsBalanced = allYearStatsTr
  trainingSetBalanced = trainingSet
}

## calculate balanced correlation 
if (balanceType > 0) {
  corrMatrixBalanced = calculateCorrelation(trainingSetBalanced, allYearStatsBalanced$IsAllStar)  
}

trainingSetBalanced = data.frame(scale(trainingSetBalanced))
validationSet = data.frame(scale(validationSet))

mlpCols <- c("Min", "Pts", "Reb", "Ast")

trainingSetBalanced = data.frame(trainingSetBalanced[ , (names(trainingSetBalanced) %in% mlpCols)])
validationSet = data.frame(validationSet[ , (names(validationSet) %in% mlpCols)])

trainingData = data.frame(trainingSetBalanced, allYearStatsBalanced$IsAllStar)
validationData = data.frame(validationSet, allYearStatsVal$IsAllStar)

# colnames(trainingData)[21] = "IsAllStar"
# colnames(validationData)[21] = "IsAllStar"

colnames(trainingData)[5] = "IsAllStar"
colnames(validationData)[5] = "IsAllStar"

for (i in 1:4) {
  print(paste0("Mlp processing:", i))
  mlpResult = MLP(trainingData = trainingData,
                  validationData = validationData,
                  learningRate = 0.2,
                  numberOfHiddenNeurons = i,
                  epoch = 1000,
                  epochLogNumber = 20,
                  targetClass = "IsAllStar",
                  numberOfOutputNodes = 1,
                  writeResult = 1,
                  filePrefix = "MLP_Balanced_Filtered_2")
}

# targetClass = "IsAllStar"
# numberOfClusters = 10
# epoch = 4000
# lerningRateDefault = 0.2
# epochLogNumber = 50
# maxKmeansIteraion = 15

for (i in 1:4) {
  print(paste0("Rbf processing:", i))
  rbfResult = RBF(trainingData = trainingData,
                  validationData = validationData,
                  numberOfClusters = i,
                  targetClass = "IsAllStar",
                  epoch = 1000,
                  lerningRateDefault = 0.2,
                  epochLogNumber = 20,
                  maxKmeansIteraion = 15,
                  writeResult = 1,
                  filePrefix = "Balanced_Filtered_2")
}

trainingDataKNN = trainingData
validationDataKNN = validationData

for (i in 2:10) {
  print(paste0("Knn processing:", i))
  knnResult = KNN(kN=i,
                  targetClass = "IsAllStar",
                  trainingData = trainingDataKNN,
                  validationData = validationDataKNN,
                  writeResult = 1)
}
