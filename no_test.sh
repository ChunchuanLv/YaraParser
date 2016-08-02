java -jar jar/YaraParser.jar parse_conll -input ../data/test.dep  -out ../data/no_model.dep  -model ../data/model/no_model_best  -punc punc_files/wsj.puncs
java -jar jar/YaraParser.jar eval -gold ../data/test.dep -parse ../data/no_model.dep -punc punc_files/wsj.puncs
