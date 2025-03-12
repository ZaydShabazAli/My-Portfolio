# Rape-Reported in India Repository

## About:
This is a mini project for SC1015 (Introduction to Data Science and Artificial Intelligence) which focuses the prediction of number of rape reported all 35 states of India from 2001-2010 using a 6-factor approach: Police Complaints, Persons Arrested for Rape, Police Corruption, Kidnapping & Abduction Cases, Police Strength, and Escapes from Police Custody. 

The mini project implements the data pipeline (except Ethical Consideration): Sample Collection, Data Preparation, Exploratory Analysis, Analytic Visualisation, Algorithmic Optimisation, Information Presentation, and Ethical Consideration.


For a detailed walkthrough, please view the source code in order from:
1. [ReadMe File](https://github.com/ZaydShabazAli/SC1015MiniProject/blob/main/README.md)
2. [Analysis of Factor 1](https://github.com/ZaydShabazAli/SC1015MiniProject/blob/main/Police_Complaints_Barnabas.ipynb) (Police Complaints)
3. [Analysis of Factor 2](https://github.com/ZaydShabazAli/SC1015MiniProject/blob/main/Persons_Arrested_For_Rape_Barnabas.ipynb) (Persons Arrested for Rape)
4. [Analysis of Factor 3](https://github.com/ZaydShabazAli/SC1015MiniProject/blob/main/Police_Corruption_Zayd.ipynb) (Police Corruption)
5. [Analysis of Factor 4](https://github.com/ZaydShabazAli/SC1015MiniProject/blob/main/Kidnapping_Victims_Zayd.ipynb) (Kidnapping & Abduction Cases) 
6. [Analysis of Factor 5](https://github.com/ZaydShabazAli/SC1015MiniProject/blob/main/Police_Strength_KeJun.ipynb) (Police Strength)
7. [Analysis of Factor 6](https://github.com/ZaydShabazAli/SC1015MiniProject/blob/main/Escapes_from_Police_Custody_KeJun.ipynb) (Escapes from Police Custody)


Data Set: https://www.kaggle.com/datasets/rajanand/crime-in-india

## Contributors:
@ClassicalAI, Barnabas
- Police Complaints, Persons Arrested for Rape (Factors 1 & 2)

@ZaydShabazAli, Zayd
-  Police Corruption, Kidnapping & Abduction Cases (Factors 3 & 4)

@yeokjunn, Ke Jun
- Police Strength, Escapes from Police Custody (Factors 5 & 6)

## Problem Definition
How and what can we use to predict the number of rape cases reported in India by state from 2001-2010, based on various factors (Police Complaints, Persons Arrested for Rape, Police Corruption, Kidnapping & Abduction Cases, Police Strength, and Escapes from Police Custody)?
Which machine learning model is the best to predict it?

## Machine Learning Models Used
Police Complaints, Persons Arrested for Rape:
- K-Nearest Neighbours
- Ridge Regression
- Support Vector Machines

Police Corruption, Kidnapping & Abduction Cases:
- Linear Regression
- K-Nearest Neighbours
- Neural Networks

Police Strength, Escapes from Police Custody:
- Linear Regression
- Polynomial Regression
- K-Nearest Neighbours

## Police Complaints Conclusion

As Rape Reported & Complaints_Received/Alleged has a strong correlation of 0.71, indicating a strong linear relationship, Complaints_Received/Alleged is an important predictor of Rape Reported.

Out of the three machine learning models implemented (KNN: Mean Squared Error - 392774666.08746356; Ridge Regression - Mean Squared Error: 125688531.65739381; SVM: Mean Squared Error - 36334198.68137042), SVM has the smallest Mean Squared Error, and is thus the best machine learning model in terms of predicting Rape Reported using Complaints_Received/Alleged.

## Persons Arrested for Rape Conclusion

As Rape Reported & Persons_Arrested has a very strong positive correlation of 0.99, indicating a very strong linear relationship, Persons_Arrested is a very important predictor of Rape Reported.

Out of the three machine learning models implemented (KNN: Mean Squared Error - 179844160.2857143; Ridge Regression - Mean Squared Error: 7325195.47854473; SVM: Mean Squared Error - 6753632.837741917), SVM has the smallest Mean Squared Error, and is thus the best machine learning model in terms of predicting Rape Reported using Persons_Arrested.

## Police Corruption Conclusion

The predictor variable, which is the total number of corruption cases reported against police, and the response variable, which is the total number of sexual assault cases reported appear to have a weak correlation of 0.40, indicating a weak to moderate linear relationship. As such, the total number of corruption cases may not be a important predictor of the number of rape cases reported.
    
Three machine learning models were utilised for algorithmic optimisation. A linear regression model, a KNN model and a neural network. Each of the three machine learning models struggle with accuratley predicting the number of sexual assault cases in the test set, which is most likely due to the low correlation between the variables. Of the models, the linear regression model appears to be the most accurate, based on the distribution of the points across the graph, as well as having the lowest mean squared error of 2604659.9424534114, compared to the mean squared error of the KNN model which is 4283014.266875, or of the neural network, which is 3543156.0000.
    
Overall, the linear regression model appears to be the most accurate, however, due to the low correlation of this variable, would not reccomend using it to predict the total number of sexual assault cases.

## Kidnapping & Abduction Cases Conclusion
The predictor variable, which is the total number of kidnapping and abduction cases reported against police, and the response variable, which is the total number of sexual assault cases reported appear to have a strong correlation of 0.72, indicating a strong linear relationship. As such, the total number of kidnapping and abduction cases may be a important predictor of the number of rape cases reported.

Three machine learning models were utilised for algorithmic optimisation. A linear regression model, a KNN model and a neural network. Each of the three machine learning models had a high mean squared error, and as a result, had difficulty accuratley predicting the total number of sexual assault cases in response to the total number of kidnapping and abduction cases. THe linear regression model had the best prediction, and had the lowest mean squared error, compared to the KNN model and the neural network.

Overall, none of the three models appears to be accurate in predicting the total number of sexual assault cases, despite the high correlation. This could be due to a number of reasons, such as a large number of anomalies which need to be filtered out further. Another possibility is that the relationship between the data is not linear, it could be quadratic or another form of clustering.

## Police Strength Conclusion

Police Strength per state and Rape Cases reported has a rather weak correlation of -0.16, which indicates that as Police Strength per state increases, rape cases reported decreases.

After using 3 different machine learning methods, I have found that the polynomial regression method (8th degree) is the best predictor of our response variable, with an explained variance of 0.49, compared to other methods such as linear regression (explained variance of 0.06) and K-Nearest Neighbours (explained variance of 0.38). Values of explained variance are calculated after removal of outliers from the dataset, for all 3 machine learning methods.

## Escapes from Police Custody Conclusion

Escapes from Police Custody per state and Rape Cases per state has a strong correlation of 0.84, which indicates that as Escapes from Police Custody increases, rape cases increases.

Using 3 different machine learning methods (K-Nearest Neighbours (KNN), Polynomial Regression, Linear Regression), I have found that the polynomial regression (6th degree) method is the best predictor of our response variable, with an explained variance of 0.83, compared to other methods such as Linear Regression (0.68) and K-Nearest Neighbours (0.79). For K-Nearest Neighbours and Polynomial Regression, I used the dataframe with outliers removed as it produced the best predictors, while for Linear Regression, I used the original dataframe as it produced the best explained variance.

## Conclusion

Out of the six predictors, the strength of the predictors (by absolute correlation value) in descending order is as follows: Persons Arrested for Rape (0.99), Escapes from Police Custody (0.84), Kidnapping & Abduction Cases (0.72), Police Complaints (0.71), Police Corruption (0.40), and Police Strength (0.16). The strongest predictor for the number of rape cases reported is the number of persons arrested for rape (Persons Arrested for Rape). Other significantly strong predictors are Escapes from Police, Kidnapping & Abduction Cases, and Police Complaints. Conversely, the weakest predictor for the number of rape cases reported is the density of police officers by state (Police Strength).  

 

For each of the strongest predictors, we want to determine the best machine learning (ML) model for predicting the number of rape cases reported. For Persons Arrested for Rape and Police Complaints, the best ML model is Suport Vector Machines (see Persons Arrested for Rape Conclusion & Police Complaints Conclusion respectively). For Escapes from Police Custody, the best ML model is Polynomial Regression (see Escapes from Police Custody conclusion). For Kidnapping & Abduction Cases, the best ML model is Linear Regression (see Kidnapping & Abduction Cases Conclusion). 

 

To best predict the number of rape cases reported in India by state from 2001-2010, Support Vector Machines model with the predictor variable: Persons Arrested for Rape can be used. As a second-best alternative, the Polynomial Regression model with the predictor variable: Escapes from Police can be used. 

## What did we learn from this project?
1)Using multiple datasets, extracting variables with different indexes, and merging them properly for accurate analysis

2)Applying data cleaning for large datasets with > 1700 rows for some datasets

3)Removal of outliers from the dataframe using methods such as Z-Score, and Inter-Quartile Range (IQR)

4)New Machine Learning Methods:
Neural Networks, K-Nearest Neighbors, Polynomial Regression, Ridge Regression, Support Vector Machines

5)Collaborating with GitHub Repositories

## References
Data Set: https://www.kaggle.com/datasets/rajanand/crime-in-india
