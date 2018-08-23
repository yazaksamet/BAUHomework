#install.packages("scatterplot3d", dependencies = TRUE)

workingDirectory = "C:\\Users\\sametyazak\\Documents\\git_workspace\\DM_Kmeans\\output"
centroidData = read.table(paste(workingDirectory , "centroid.txt", sep="\\"))
irisData = read.table(paste(workingDirectory , "data.txt", sep="\\"))

cols = c("SepalLength","SepalWidth","PetalLength","PetalWidth","ClusterIndex")

colnames(centroidData) <- cols
colnames(irisData) <- cols


#irisData
#centroidData

#scatterplot3d(x = irisData$SepalLength, y = irisData$SepalWidth, z = irisData$PetalLength, color="green")
plt3d <- scatterplot3d(x = centroidData$SepalWidth, y = centroidData$PetalLength, 
		z = centroidData$PetalWidth, 
		color="black",
		main="Iris Data 4",
		pch=19,
		xlab="Sepal Width",
            ylab="Petal Length",
            zlab="Petal Width",
		type="h"
		)

for(i in 1:dim(irisData)[1])  # for each row
{
	point_color = "black"
	
	if (irisData[i,5] == 0) {
		point_color = "red"
	}
	else if (irisData[i,5] == 1) {
		point_color = "blue"
	}
	else if (irisData[i,5] == 2) {
		point_color = "green"
	}

	plt3d$points3d(x = irisData[i,2], y = irisData[i,3], z = irisData[i,4], col=point_color)

}