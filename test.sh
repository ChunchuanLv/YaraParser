java -jar jar/YaraParser.jar parse_partial -input ../data/test.dep  -out ../data/vecModel.dep  -model ../data/model/vecModel  -punc punc_files/wsj.puncs
java -jar jar/YaraParser.jar eval -gold ../data/test.dep -parse ../data/vecModel.dep -punc punc_files/wsj.puncs

