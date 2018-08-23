RBF <- function(trainingData, 
                validationData, 
                numberOfClusters,
                targetClass,
                epoch, 
                lerningRateDefault, 
                epochLogNumber,
                maxKmeansIteraion,
                writeResult,
                filePrefix)
{
  print("RBF Start:")
  print(date())
  targetCols <- c(targetClass)
  
  trainingSet = trainingData[ , !(names(trainingData) %in% targetCols)]
  validationSet = validationData[ , !(names(validationData) %in% targetCols)]
  
  trainingOutputSet = data.frame(trainingData[ , (names(trainingData) %in% targetCols)])
  validationOutputSet = data.frame(validationData[ , (names(validationData) %in% targetCols)])
  
  clusterCentroids = matrix(sample(c(0:0), numberOfClusters, TRUE), numberOfClusters, dim(trainingSet)[2])
  clusterSpreads = matrix(sample(c(0:0), numberOfClusters, TRUE), numberOfClusters, 1)
  trainingClusters = matrix(sample(c(0:0), dim(trainingSet)[1], TRUE), dim(trainingSet)[1], 1)
  clusterCentroids =data.frame(clusterCentroids)
  
  clusterChanges = matrix(sample(c(0:0), maxKmeansIteraion, TRUE), maxKmeansIteraion, 1)
  
  for (c in 1:numberOfClusters) {
    randomCentroidIndex =sample(1:dim(trainingSet)[1], 1);
    clusterCentroids[c,] = data.frame(trainingSet[randomCentroidIndex,])
  }
  
  numberOfClusterChanges = 0
  kmeansIterationCount = 0
  
  while (kmeansIterationCount == 0 || (numberOfClusterChanges > 0 && kmeansIterationCount < maxKmeansIteraion)) {
    kmeansIterationCount = kmeansIterationCount + 1
    print(paste0("k ite: ", kmeansIterationCount))
    numberOfClusterChanges = 0
    
    clusterCentroidTotals = matrix(sample(c(0:0), numberOfClusters, TRUE), numberOfClusters, dim(trainingSet)[2])
    clusterCentroidCount = matrix(sample(c(0:0), numberOfClusters, TRUE), numberOfClusters, 1)
    
    for (i in 1:dim(trainingSet)[1]) {
      ## find closest cluster
      #print(paste0("i : ", i))  
      innerClusterIndex = 0
      inputValue = as.numeric(trainingSet[i,])
      bestDistance = -1
      
      for (c2 in 1:numberOfClusters) {
        clusterDistance = sqrt(sum((inputValue - clusterCentroids[c2,])^2))
        
        if (bestDistance == -1 || clusterDistance < bestDistance) {
          innerClusterIndex = c2
          bestDistance = clusterDistance
        }
      }
      
      # assign found cluster
      if (trainingClusters[i,1] != innerClusterIndex) {
        trainingClusters[i,1] = innerClusterIndex
        numberOfClusterChanges = numberOfClusterChanges + 1
      }
      
      clusterCentroidTotals[innerClusterIndex,] = clusterCentroidTotals[innerClusterIndex,] + inputValue
      clusterCentroidCount[innerClusterIndex,1] = clusterCentroidCount[innerClusterIndex,1] + 1
      
      # calculate most distant instance for spread
      if (bestDistance > clusterSpreads[innerClusterIndex, 1]) {
        clusterSpreads[innerClusterIndex, 1] = bestDistance
      }
      
    }
    
    clusterChanges[kmeansIterationCount,1] = numberOfClusterChanges
    
    ## re-calculate centroid values 
    for (c3 in 1:numberOfClusters) {
      if (clusterCentroidCount[c3,1] != 0) {
        clusterCentroids[c3, ] = clusterCentroidTotals[c3,] / clusterCentroidCount[c3,1]
      }
      else {
        clusterCentroids[c3, ] = 0
      }
    }
  }
  
  ## calculate cluster spreads 
  
  clusterSpreads = clusterSpreads / 2
  
  # + 1 means bias unit
  trainingDataPerceptives = matrix(sample(c(0:0), numberOfClusters + 1, TRUE), dim(trainingSet)[1], numberOfClusters + 1)
  testDataPerceptives = matrix(sample(c(0:0), numberOfClusters + 1, TRUE), dim(validationSet)[1], numberOfClusters + 1)
  
  # assign bias unit
  trainingDataPerceptives[,1] = 1
  testDataPerceptives[,1] = 1
  
  for (pi in 1:dim(trainingSet)[1]) {
    for (ph in 1:numberOfClusters) {
      instanceDistance = sqrt(sum((as.numeric(trainingSet[pi,]) - clusterCentroids[ph,])^2))
      pht = exp(-((instanceDistance^2) / (2*(clusterSpreads[ph, 1]^2))))
      trainingDataPerceptives[pi,ph+1] = pht
    }
  }
  
  for (pi in 1:dim(validationSet)[1]) {
    for (ph in 1:numberOfClusters) {
      instanceDistance = sqrt(sum((as.numeric(validationSet[pi,]) - clusterCentroids[ph,])^2))
      pht = exp(-((instanceDistance^2) / (2*(clusterSpreads[ph, 1]^2))))
      testDataPerceptives[pi,ph+1] = pht
    }
  }
  
  ## train sinple layer perceptron 
  
  # weight initialization
  
  inputWeight = matrix(sample(c(-100:100), numberOfClusters + 1, TRUE) / 10000, 1, numberOfClusters + 1)
  
  # create error matrix
  errorMatrix = matrix(0, ncol = 14, nrow = ((epoch / epochLogNumber)))
  errorSet = data.frame(errorMatrix)
  colnames(errorSet)<-c("K","epoch","Train-TP", "Train-FP", "Train-TN", "Train-FN", "Train-%", "CE-Train",
                        "Val-TP", "Val-FP", "Val-TN", "Val-FN", "Val-%", "CE-Val")
  
  #run epochs
  learningRate = lerningRateDefault
  
  for (e in 1:epoch){
    for(x in 1:dim(trainingDataPerceptives)[1]) {
      y = sigmoid(sum(inputWeight[1,] * trainingDataPerceptives[x,]))
      r = trainingOutputSet[x,1]
      
      deltaWeight = learningRate*(r-y)*trainingDataPerceptives[x,]
      inputWeight = inputWeight + deltaWeight
      
      #trainingData[x,4] = y
    }
    
    if (e %% epochLogNumber == 0) {
      print(paste0("K: ", numberOfClusters, ", epoch:", e))
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
      
      # calculate training data
      for(x in 1:dim(trainingData)[1]) {
        y = sigmoid(sum(inputWeight[1,] * trainingDataPerceptives[x,]))
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
      
      for(x in 1:dim(validationSet)[1]) {
        y = sigmoid(sum(inputWeight[1,] * testDataPerceptives[x,]))
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
      
      # log error
      epochLogIndex = (e / epochLogNumber)
      errorSet[epochLogIndex,1] = numberOfClusters
      errorSet[epochLogIndex,2] = e
      errorSet[epochLogIndex,3] = trainTP
      errorSet[epochLogIndex,4] = trainFP
      errorSet[epochLogIndex,5] = trainTN
      errorSet[epochLogIndex,6] = trainFN
      errorSet[epochLogIndex,7] = (trainTN + trainTP) / (trainTN + trainTP + trainFN + trainFP)
      errorSet[epochLogIndex,8] = trainingErrorSum
      errorSet[epochLogIndex,9] = valTP
      errorSet[epochLogIndex,10] = valFP
      errorSet[epochLogIndex,11] = valTN
      errorSet[epochLogIndex,12] = valFN
      errorSet[epochLogIndex,13] = (valTN + valTP) / (valTN + valTP + valFN + valFP)
      errorSet[epochLogIndex,14] = validationErrorSum
    }
    
    learningRate = learningRate * 0.998
    #print(paste0("K: ", numberOfClusters, ", epoch:", e))
  }
  
  print("RBF End:")
  print(date())
  
  result <- list("Error" = errorSet, 
                 "Spread" = clusterSpreads,
                 "Weight" = inputWeight,
                 "Centroid" = clusterCentroids,
                 "ClusterIteration" = kmeansIterationCount,
                 "NumberOfClusters" = numberOfClusters,
                 "LearningRate" = lerningRateDefault,
                 "MaxClusterIterations" = maxKmeansIteraion,
                 "TargetClass" = targetClass,
                 "Epoch" = epoch,
                 "ClusterChanges" = clusterChanges
                 )
  
  if (writeResult == 1) {
    errorFileName = paste0(filePrefix,"_Error_K_", numberOfClusters, "_Epoch_", e, ".csv")
    write.csv(errorSet, errorFileName)
    
    clusterFileName = paste0(filePrefix,"_Cluster_K_", numberOfClusters, "_Epoch_", e, ".csv")
    write.csv(clusterChanges, clusterFileName)
  }
  
  return (result)
}