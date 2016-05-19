import codepred.RNNPredictor as rnn

data_train = "./data/train.txt"
data_test = "./data/val.txt"

rnn.train_model(data_train, data_test, "./models/", "api_pred")