<!-- Space -->

# Supported MIME Types

As the spec mentions, this only supports `text/plain`.

## URL: `http://localhost/example.txt`

Before the POST request - `public/example.txt`

    cereal is definitely not a soup

After the POST request - `public/example.txt`

![after POST request](markdown/post_after.png)

## (ERROR - 415) URL: `http://localhost/index.html`

![415](markdown/post_415.png)


## (ERROR - 404) URL: `http://localhost/index.h`

![404](markdown/post_404.png)