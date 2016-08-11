java -jar  jar/YaraParser.jar train nt:16 -input ../data/test.dep -train-file /disk/cohortnas/masters/s1544871/train.dep -dev /disk/cohortnas/masters/s1544871/dev.dep  -model /disk/cohortnas/masters/s1544871/matModel iter:20 -punc punc_files/wsj.puncs -depe depsF.dep -repPath ../data  depMat
java -jar jar/YaraParser.jar parse_conll  -input ../data/test.dep  -out ../data/matModel.dep  -model /disk/cohortnas/masters/s1544871/matModel_best  -punc punc_files/wsj.puncs
java -jar jar/YaraParser.jar eval  -gold ../data/test.dep -parse ../data/matModel.dep -punc punc_files/wsj.puncs

