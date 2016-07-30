java -jar  jar/YaraParser.jar train -train-file ../data/train.dep -dev ../data/dev.dep  -model ../data/model/vecModel iter:10 -punc punc_files/wsj.puncs -repPath ../data 
java -jar jar/YaraParser.jar parse_partial -input ../data/test.dep  -out ../data/vecModel.dep  -model ../data/model/vecModel_best  -punc punc_files/wsj.puncs
java -jar jar/YaraParser.jar eval -gold ../data/test.dep -parse ../data/vecModel.dep -punc punc_files/wsj.puncs

