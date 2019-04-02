# WSE_Indexing

See the blog http://www.chaoqunhuang.com/blog/build-inverted-index-in-java.html for detailed description


## Prepare

1. Change the file paths in `config.properties`.

2. Create dictionary
```
mkdir index
mkdir wet
mkdir IntermediatePosting
```

3. Enter the folder wet and download data.
```
cd wet
wget https://commoncrawl.s3.amazonaws.com/crawl-data/CC-MAIN-2017-39/segments/1505818685129.23/wet/CC-MAIN-20170919112242-20170919132242-00000.warc.wet.gz
gunzip CC-MAIN-20170919112242-20170919132242-00000.warc.wet.gz
```

## Explanation

The main file is the main funciton of this project.

1. Use CommonCralReader to read the data from the source files.
2. Parse data and post the data to intermediate files. And insert the formated data into MongoDB.
3. Sort the data in intermediate files and build the Lexicon Index.
