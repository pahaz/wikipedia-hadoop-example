# Wikipedia TF-IDF Index #

Java example of making wikipedid TF-IDF word index with Apache Hadoop MapReduce.

 - `WikiNumberOfDocs` - evaluate number of docs
 - `WikiWordTFIDFIndex` - evaluate TF-IDF word index

See [build.log](https://github.com/pahaz/wikipedid-hadoop-example/blob/master/build.log.txt) and [result.log](https://github.com/pahaz/wikipedid-hadoop-example/blob/master/result.log.txt).


# How-to run #

    git clone https://github.com/pahaz/wikipedid-hadoop-example.git
    cd wikipedid-hadoop-example
    
    mvn compile
    mvn package
    
    hadoop fs -rm -r /user/s0073/w_count_words || echo "No"
    hadoop fs -rm -r /user/s0073/w_tfidf || echo "No"
    hadoop jar target/hhd-1.0-SNAPSHOT.jar WikiNumberOfDocs   /data/wiki/ru/articles /user/s0073/w_count_words
    hadoop jar target/hhd-1.0-SNAPSHOT.jar WikiWordTFIDFIndex /data/wiki/ru/articles /user/s0073/w_count_words/part-00000 /user/s0073/w_tfidf

see [build.log](https://github.com/pahaz/wikipedid-hadoop-example/blob/master/build.log.txt)

    
# Source Data format #

 - `/data/wiki/ru/articles` contains strings like `doc_id \t doc_content` (without spaces around `\t`)

# Generated Data formats #

 - `/user/s0073/w_count_words/part-00000 ` - contains one string `Count: \t number_of_docs` (without spaces around `\t`)
 - `/user/s0073/w_tfidf` - contains strings like `word \t doc_id1:tfidf \t doc_id2:tfidf ...` (without spaces around `\t`)
 
# How-to view results #

    # hadoop fs -cat /user/s0073/w_tfidf*
    hadoop fs -cat /user/s0073/w_count_words/*

see [result.log](https://github.com/pahaz/twitter-hadoop-example/blob/master/result.log.txt)

# Time #

Hadoop work on 8x nodes: 2x Dual-core AMD Opteron 285 2.6 GHz, 8 GB RAM, 150 GB HDD.

* Files *  
 - `/data/twitter/twitter_rv.net` - 24.4 G
 - `/user/s0073/count` - 35.6 M * 13 (parts 00000 - 00012)

* Time *  
 - `TwitterFollowerCounter` - 22:32:52 - 23:02:35 (0:29:43)
 - `TwitterAvgFollowers` - 23:02:43 - 23:04:24 (0:01:41)
 - `TwitterTopFollowers` - 23:04:31 - 23:06:50 (0:02:19)
 - `TwitterFollowerCounterGroupByRanges` - 23:06:57 - 23:08:06 (0:01:09)


# Other #

Sometime you need up hadoop job priority. Use this command:

    mapred job -set-priority <job_id> VERY_HIGH
