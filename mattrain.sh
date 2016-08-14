java -jar  jar/YaraParser.jar train -test  /disk/cohortnas/masters/s1544871/test.dep -train-file /disk/cohortnas/masters/s1544871/train.dep -dev /disk/cohortnas/masters/s1544871/dev.dep  -model /disk/cohortnas/masters/s1544871/matModel iter:20 -punc punc_files/wsj.puncs -ce deps.words -depe depsF.dep -repPath ../data  depMat  nt:16
java -jar jar/YaraParser.jar parse_conll  -input /disk/cohortnas/masters/s1544871/test.dep  -out ./disk/cohortnas/masters/s1544871/matModel.dep  -model /disk/cohortnas/masters/s1544871/matModel_best  -punc punc_files/wsj.puncs
java -jar jar/YaraParser.jar eval  -gold ../data/test.dep -parse ../data/matModel.dep -punc punc_files/wsj.puncs

