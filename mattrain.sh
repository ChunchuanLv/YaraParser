java -jar  jar/YaraParser.jar train nt:16  -train-file ../data/train.dep -dev ../data/dev.dep  -model ../data/model/matModel iter:20 -punc punc_files/wsj.puncs -depe depsF.dep -repPath ../data  depMat
java -jar jar/YaraParser.jar parse_conll  -input ../data/test.dep  -out ../data/matModel.dep  -model ../data/model/matModel_best  -punc punc_files/wsj.puncs
java -jar jar/YaraParser.jar eval  -gold ../data/test.dep -parse ../data/matModel.dep -punc punc_files/wsj.puncs

