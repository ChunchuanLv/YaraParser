java -jar  jar/YaraParser.jar train nt:16  -test ../data/test.dep -train-file /disk/cohortnas/masters/s1544871/train.dep -dev /disk/cohortnas/masters/s1544871/dev.dep  -model /disk/cohortnas/masters/s1544871/vecModel iter:20 -punc punc_files/wsj.puncs  -repPath ../data  
java -jar jar/YaraParser.jar parse_conll -input ../data/test.dep  -out ../data/vecModel.dep  -model /disk/cohortnas/masters/s1544871/vecModel_best  -punc punc_files/wsj.puncs
java -jar jar/YaraParser.jar eval -gold ../data/test.dep -parse ../data/vecModel.dep -punc punc_files/wsj.puncs

