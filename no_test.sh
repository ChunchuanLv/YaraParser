java -jar jar/YaraParser.jar parse_partial -input ../data/test.dep  -out ../data/no_model.dep  -model ../data/model/no_mmodel  -punc punc_files/wsj.puncs
java -jar jar/YaraParser.jar eval -gold ../data/test.dep -parse ../data/no_mmodel.dep -punc punc_files/wsj.puncs
