# imdbForumCrawler
## Info
Collecting threads and their comments on the Awards-Board of the IMDB-Forum.
## Output
For each tweet there is a JSONObject as follows:
```javascript
{
  "_id": "228093339",
  "title": "What was the last Oscar winning/nominated film you've seen?",
  "replies": 578,
  "authorName": "Alexander_Blanchett",
  "comments": [
    {
      "content": "Misery Won Oscar for Best Actress in a Leading Role My Rating - 7/10.",
      "timestamp": NumberLong(1396846140000),
      "id": "228094119",
      "title": "Re: <span>What was the last Oscar winning/nominated film you've seen?</span>",
      "posterId": "ur13471683",
      "posterName": "Bunty-at-IMDB"
    },
    {
      "content": "Secrets and Lies. saw it this morning.",
      "timestamp": NumberLong(1396846980000),
      "id": "228094567",
      "title": "Re: <span>What was the last Oscar winning/nominated film you've seen?</span>",
      "posterId": "ur28141289",
      "posterName": "BrandonLee_Dizuncan"
    },
    ...
  ],
  "authorId": "ur1296280"
}
```
## Build
run `mvn install` and use the ...jar-with-dependencies.jar; or take the imdbForumCrawler-0.0.1.jar from the repository if you don't want to build it yourself.
## Run
`java -jar imdbForumCrawler-0.0.1.jar [host] [port] [userName] [password] [database] [pathToSearch.txt]`

e.g. `java -jar imdbForumCrawler-0.0.1.jar bitnami-meanstack-1842.cloudapp.net 27017 oscar 123456 oscar`

