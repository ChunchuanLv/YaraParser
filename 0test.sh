java -jar YaraParser.jar parse_partial -input ../data/test.dep  -out ../data/0model.dep  -model ../data/model/0model  -punc punc_files/wsj.puncs
java -jar YaraParser.jar eval -gold ../data/test.dep -parse ../data/0model.dep -punc punc_files/wsj.puncs
