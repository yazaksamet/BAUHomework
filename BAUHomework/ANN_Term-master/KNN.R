KNN <- function(kN, targetClass, trainingData, validationData, writeResult)
{
  targetCols <- c(targetClass)
  
  knnTempResult = data.frame(matrix(sample(c(0:0), dim(trainingData)[1], TRUE), dim(trainingData)[1], 2))
  colnames(knnTempResult) <- c("Distance", "Target")
  
  trainingSet = trainingData[ , !(names(trainingData) %in% targetCols)]
  validationSet = validationData[ , !(names(validationData) %in% targetCols)]
  
  trainingOutputSet = data.frame(trainingData[ , (names(trainingData) %in% targetCols)])
  validationOutputSet = data.frame(validationData[ , (names(validationData) %in% targetCols)])
  
  tp = 0
  fp = 0
  tn = 0
  fn = 0
  
  for (i in 1:dim(validationSet)[1])
  {
    for (j in 1:dim(trainingSet)[1]) 
    {
      distance = eucledeanDistance(validationSet[i,], trainingSet[j,])
      knnTempResult[j,1] = distance
      knnTempResult[j,2] = trainingOutputSet[j,1]
    }
    
    nearestNeighbours = head(knnTempResult[order(knnTempResult$Distance, decreasing = FALSE),], n = kN)
    
    positiveInstances = nearestNeighbours[nearestNeighbours$Target == 1, ]
    negativeInstances = nearestNeighbours[nearestNeighbours$Target == 0, ]
    
    validationInstanceResult = 0
    
    if (dim(positiveInstances)[1] > dim(negativeInstances)[1]) {
      validationInstanceResult = 1
    }
    
    if (validationInstanceResult == validationOutputSet[i,1] && validationInstanceResult == 1) {
      tp = tp + 1
    } else if (validationInstanceResult != validationOutputSet[i,1] && validationInstanceResult == 1) {
      fp = fp + 1 
    } else if (validationInstanceResult == validationOutputSet[i,1] && validationInstanceResult == 0) {
      tn = tn + 1
    } else if (validationInstanceResult != validationOutputSet[i,1] && validationInstanceResult == 0) {
      fn = fn + 1
    }
    
    if (i %% 100 == 0) {
      print (i)
    }
  }
  
  result <- list("tp" = tp, "fp" = fp, "tn" = tn, "fn" = fn, "percentage" = (tp + tn) / (tp + tn + fp + fn))
  
  if (writeResult == 1) {
    fileName = paste0("KNN_", kN, ".csv")
    write.csv(result, fileName)
  }
  
  return (result)
}