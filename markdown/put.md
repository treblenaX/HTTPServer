<!-- Space -->

## URL: `http://localhost/example.txt`

Before the PUT request (after the POST request from before)- `public/example.txt`

    cereal is definitely not a soup cereal is a soup >:(

After the PUT request on existing file - `public/example.txt`

![PUT_AFTER](markdown/put_after_OK.png)

After the PUT request on new file - `public/hot_dog.txt`

![Hot Dog](markdown/put_after_CREATED.png)


## Handles `404` for `FILE NOT FOUND` and `500` error for `INTERNAL SERVER ERROR`.