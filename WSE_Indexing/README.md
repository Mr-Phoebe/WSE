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


