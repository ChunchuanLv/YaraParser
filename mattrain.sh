java -jar  jar/YaraParser.jar train -train-file ../data/train.dep -dev ../data/dev.dep  -model ..data/model/matModel iter:20 -punc punc_files/wsj.puncs -repPath ../data  depMat
java -jar jar/YaraParser.jar parse_partial -input ../data/test.dep  -out ../data/matModel.dep  -model ../data/model/matModel_best  -punc punc_files/wsj.puncs
java -jar jar/YaraParser.jar eval -gold ../data/test.dep -parse ../data/matModel.dep -punc punc_files/wsj.puncs

