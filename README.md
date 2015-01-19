# Wikipedia TF-IDF Index #

Java example of making wikipedia TF-IDF word index with Apache Hadoop MapReduce.

 - `WikiNumberOfDocs` - evaluate number of docs
 - `WikiWordTFIDFIndex` - evaluate TF-IDF word index

See [build.log](https://github.com/pahaz/wikipedia-hadoop-example/blob/master/build.log.txt) and [result.log](https://github.com/pahaz/wikipedia-hadoop-example/blob/master/result.log.txt) - first build log (without fix2bugs - top 21 docs for one word).

See [build.log](https://github.com/pahaz/wikipedia-hadoop-example/blob/master/build2.log.txt) and [result.log](https://github.com/pahaz/wikipedia-hadoop-example/blob/master/result2.log.txt) - first build log (with fix2bugs - top 20 docs for one word).


# How-to run #

    git clone https://github.com/pahaz/wikipedia-hadoop-example.git
    cd wikipedia-hadoop-example
    
    mvn compile
    mvn package
    
    hadoop fs -rm -r /user/s0073/w_count_words_ru || echo "No"
    hadoop fs -rm -r /user/s0073/w_count_words_en || echo "No"
    hadoop fs -rm -r /user/s0073/w_tfidf_ru || echo "No"
    hadoop fs -rm -r /user/s0073/w_tfidf_en || echo "No"
    hadoop jar target/hhd-1.0-SNAPSHOT.jar WikiNumberOfDocs   /data/wiki/ru/articles /user/s0073/w_count_words_ru
    hadoop jar target/hhd-1.0-SNAPSHOT.jar WikiNumberOfDocs   /data/wiki/en/articles /user/s0073/w_count_words_en
    hadoop jar target/hhd-1.0-SNAPSHOT.jar WikiWordTFIDFIndex /data/wiki/ru/articles /user/s0073/w_count_words_ru/part-00000 /user/s0073/w_tfidf_ru
    hadoop jar target/hhd-1.0-SNAPSHOT.jar WikiWordTFIDFIndex /data/wiki/en/articles /user/s0073/w_count_words_en/part-00000 /user/s0073/w_tfidf_en

see [build.log](https://github.com/pahaz/wikipedia-hadoop-example/blob/master/build.log.txt)

    
# Source Data format #

 - `/data/wiki/*/articles` contains strings like `doc_id \t doc_content` (without spaces around `\t`)

 
# Generated Data formats #

 - `/user/s0073/w_count_words_*/part-00000 ` - contains one string `Count: \t number_of_docs` (without spaces around `\t`)
 - `/user/s0073/w_tfidf_*` - contains strings like `word \t doc_id1:tfidf \t doc_id2:tfidf ...` (without spaces around `\t`)
 
 
# How-to view results #

    # hadoop fs -cat /user/s0073/w_tfidf_ru/*
    hadoop fs -cat /user/s0073/w_count_words_*/*

see [result.log](https://github.com/pahaz/twitter-hadoop-example/blob/master/result.log.txt)


# Time #

Hadoop work on 8x nodes: 2x Dual-core AMD Opteron 285 2.6 GHz, 8 GB RAM, 150 GB HDD.

* Files *  
 - `/data/wiki/ru/articles` - 5.3 G
 - `/data/wiki/en/articles` - 13.1 G
 - `/user/s0073/w_tfidf_ru` - 35.6 M * 13 (parts 00000 - 00012)
 - `/user/s0073/w_tfidf_en` - 51.4 M * 13 (parts 00000 - 00012)

* Time *  
 - `WikiNumberOfDocs` - ru : 19:33:48 - 19:34:39 (0:00:51); en : 19:35:03 - 19:36:49 (0:01:46)
 - `WikiWordTFIDFIndex` - ru : 19:37:12 - 19:41:07 (0:03:55); en : 19:41:30 - 19:53:03 (0:11:33)

see [result.log](https://github.com/pahaz/twitter-hadoop-example/blob/master/result.log.txt) and [build.log](https://github.com/pahaz/wikipedia-hadoop-example/blob/master/build.log.txt)


# How it works #

*Definitions*

 - `D` - collection of docs
 - `d` - element of D (one doc form collection)
 - `w` - term (one word)
 - `freq(w, d)` - row term frequency (row word frequency in one doc)
 - `tf(w, d)` - term frequency (word frequency in one doc)
 - `idf(w, D)` - inverse document frequency (inverse value of word frequency in all docs)

*Formulas*

`tf(w, d) = 0.5 + 0.5 * freq(w, d) / max( freq(wi, d) for wi in d )` - [code](https://github.com/pahaz/wikipedia-hadoop-example/blob/master/src/main/java/WikiWordTFIDFIndex.java#L92)

`idf(w, D) = LOG( total_number_of_docs / number_of_docs_where_word_appears )` - [code](https://github.com/pahaz/wikipedia-hadoop-example/blob/master/src/main/java/WikiWordInfo.java#L59)

*Map*: `(doc1, doc1_content)` -> `[[word1, [idf=?, doc1:tf(word1, doc1)]], ..., [wordN, [idf=?, doc1:tf(wordN, doc1)]]]`

*Reduce*: `(word1, [[idf1, doc1:tf], ..., [idf1, docK:tf]])` -> `(word1, [new_idf, doc1:tf, ..., docK:tf])`


# Other #

If you need increase hadoop job priority use command:

    mapred job -set-priority <job_id> VERY_HIGH

If you want merge w_count_words_ru results use command:

    hadoop fs -cat /user/s0073/w_count_words_ru/* | hadoop fs -put - /user/s0073/w_count_words_ru.txt

If you want merge w_count_words_ru and save results in local fs use command:

    hadoop fs -getmerge /user/s0073/w_count_words_ru w_count_words_ru.txt
