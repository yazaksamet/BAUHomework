setwd("C:/Users/sametyazak/Desktop/ynwa/bau/2017 - Spring/ann/term project/")

# plot knn

knnResult = data.frame()

for (i in 1:10) {
  filePath = paste0("results/KNN_", i, ".csv")
  innerKnnResult = read.csv(file=filePath, header=TRUE, sep=",")
  
  if (length(knnResult) == 0) {
    knnResult = data.frame(innerKnnResult)    
  } else {
    innerKnnResult[1,1] = i
    knnResult = rbind(knnResult, innerKnnResult)
  }
}

plot(data.frame(knnResult$X, knnResult$percentage),type="l",col=4, lty = 2, xlab="K Neighbor", ylab="Success Rate")
colnames(knnResult) <- c("K", "True Positive", "False Positive", "True Negative", "False Negative", "%SuccessRate")

knnOutFile = paste0("KNN_min_out.csv")
write.csv(knnResult, knnOutFile)


# plot mlp

for (i in 1:4) {
  filePath = paste0("results/MLP_Balanced_Filtered_2_Error_H_", i, "_Epoch_1000.csv")
  innerMlpResult = read.csv(file=filePath, header=TRUE, sep=",")
  
  trainPlot = data.frame(innerMlpResult$epoch, innerMlpResult$Train..)
  valPlot = data.frame(innerMlpResult$epoch, innerMlpResult$Val..)
  
  maxValidationError = max(valPlot[,2])
  minValidationError = min(valPlot[,2])
  
  maxTrainingError = max(trainPlot[,2])
  minTrainingError = min(trainPlot[,2])
  
  minError = min(minValidationError,minTrainingError) - 0.5
  maxError = max(maxTrainingError,maxValidationError) + 0.5
  
  plot(trainPlot,type="l",col=4, lty = 2, xlab="Epoch", ylab="Error Rate")
  
  lines(valPlot,col=6,lty = 1, pch = 4)
  title(paste0("Number of Hidden Neurons: ", i))
  legend("topright", c("Training", "Validation"), col = c(4, 6),
         text.col = "black", lty = c(2,1),
         bg = "gray90", cex = 0.5)
  
  
}

# plot rbf

for (i in 1:3) {
  filePath = paste0("results/Balanced_Filtered_2_Error_K_", i, "_Epoch_1000.csv")
  innerMlpResult = read.csv(file=filePath, header=TRUE, sep=",")
  
  trainPlot = data.frame(innerMlpResult$epoch, innerMlpResult$Train..)
  valPlot = data.frame(innerMlpResult$epoch, innerMlpResult$Val..)
  
  plot(trainPlot,type="l",col=4, lty = 2, xlab="Epoch", ylab="Success Rate")
  
  lines(valPlot,col=6,lty = 1, pch = 4)
  title(paste0("Number of K: ", i))
  legend("topright", c("Training", "Validation"), col = c(4, 6),
         text.col = "black", lty = c(2,1),
         bg = "gray90", cex = 0.5)
  
  
}


#plot balanced data with full set

for (i in 3:9) {
  filePath = paste0("results/Balanced_Full_Error_K_", i, "_Epoch_4000.csv")
  innerMlpResult = read.csv(file=filePath, header=TRUE, sep=",")
  
  trainPlot = data.frame(innerMlpResult$epoch, innerMlpResult$Train..)
  valPlot = data.frame(innerMlpResult$epoch, innerMlpResult$Val..)
  
  plot(trainPlot,type="l",col=4, lty = 2, xlab="Epoch", ylab="Success Rate")
  
  lines(valPlot,col=6,lty = 1, pch = 4)
  title(paste0("Number of K: ", i))
  legend("topright", c("Training", "Validation"), col = c(4, 6),
         text.col = "black", lty = c(2,1),
         bg = "gray90", cex = 0.5)
  
  
}



#plot unbalanced data with full set

for (i in 3:9) {
  filePath = paste0("results/UnBalanced_Full_Error_K_", i, "_Epoch_4000.csv")
  innerMlpResult = read.csv(file=filePath, header=TRUE, sep=",")
  
  trainPlot = data.frame(innerMlpResult$epoch, innerMlpResult$Train..)
  valPlot = data.frame(innerMlpResult$epoch, innerMlpResult$Val..)
  
  plot(trainPlot,type="l",col=4, lty = 2, xlab="Epoch", ylab="Success Rate", ylim=c(0,1))
  
  lines(valPlot,col=6,lty = 1, pch = 4)
  title(paste0("Number of K: ", i))
  legend("topright", c("Training", "Validation"), col = c(4, 6),
         text.col = "black", lty = c(2,1),
         bg = "gray90", cex = 0.5)
  
  
}


filePath = paste0("results/MLP_Balanced_Error_H_10_Epoch_10.csv")
innerMlpResult = read.csv(file=filePath, header=TRUE, sep=",")

mlpPlot = data.frame(innerMlpResult$epoch, (innerMlpResult$Train.TP + innerMlpResult$Train.TN) / 
                       (innerMlpResult$Train.FP + innerMlpResult$Train.FN + innerMlpResult$Train.TP + innerMlpResult$Train.TN),
                     (innerMlpResult$Val.TP + innerMlpResult$Val.TN) / 
                       (innerMlpResult$Val.FP + innerMlpResult$Val.FN + innerMlpResult$Val.TP + innerMlpResult$Val.TN)
                     
                     )

colnames(mlpPlot) <- c("Epoch", "Train", "Val")

trainPlot = data.frame(mlpPlot$Epoch, mlpPlot$Train)
valPlot = data.frame(mlpPlot$Epoch, mlpPlot$Val)

plot(trainPlot,type="l",col=4, lty = 2, xlab="Epoch", ylab="Success Rate", ylim=c(0,1))

lines(valPlot,col=6,lty = 1, pch = 4)
title(paste0("Number of H: ", 10))
legend("topright", c("Training", "Validation"), col = c(4, 6),
       text.col = "black", lty = c(2,1),
       bg = "gray90", cex = 0.5)
