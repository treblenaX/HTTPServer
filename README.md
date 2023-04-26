<h1>HTTP Server</h1>
<p>Please check the individual markdown files or refer to the dropdowns below...</p>
<details>
                <summary>How to Run...</summary>
                    <!-- Space -->

`javac ./*.java ./threads/*.java ; java SocketServer.java` on the root level of `/HTTPServer`</details>
<details>
                <summary>GET</summary>
                    <!-- Space -->

# Supported MIME Types

Technically, this server has no restrictions on what MIME types are restricted. 

    contentType = Files.probeContentType(file.toPath());    // automatically convert filename to `Content-Type` form

Here are some examples of what can be fetched with GET.

---

## URL: `http://localhost/example.txt`
>`text/plain`

Notice how the text file contents is fetched and displayed on the browser.

![Plain Text Image](markdown/text_plain.png)

---

## URL: `http://localhost/index.html`
>`text/html` 
>
>`image/jpeg`
>
>`image/gif`

Calling the [index HTML page](public/index.html) using `http://localhost/index.html` and then the HTML page itself is calling the [PNG image](public/satoru_gojo.jpg) and the [GIF image](public/jujutsu-kaisen-funny.gif) inside the HTML.

    <div id="img_container">
        <!-- HTML Q4 -->
        <img src="./satoru_gojo.jpg" alt="Main image of Satoru Gojo"/>
        <div id="img_padding"></div>
        <img src="./jujutsu-kaisen-funny.gif" alt="Funny GIF of Satoru Gojo"/>
    </div>

>The `./satoru_gojo.jpg` and `jujutsu-kaisen-funny.gif` are relative paths that is in the `/public` folder and on the same level as the `index.html`.

![Index HTML image](markdown/index_html.png)

---

## (ERROR) URL: `http://localhost/example.tx`

Notice how there is no file with the name `example.tx` within the `/public` folder. Therefore, the server returns an error code of `404 NOT FOUND` along with the corresponding HTTPCat.

![error 404 page](markdown/get_404.png)
</details>
<details>
                <summary>POST</summary>
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

![404](markdown/post_404.png)</details>
<details>
                <summary>PUT</summary>
                    <!-- Space -->

## URL: `http://localhost/example.txt`

Before the PUT request (after the POST request from before)- `public/example.txt`

    cereal is definitely not a soup cereal is a soup >:(

After the PUT request on existing file - `public/example.txt`

![PUT_AFTER](markdown/put_after_OK.png)

After the PUT request on new file - `public/hot_dog.txt`

![Hot Dog](markdown/put_after_CREATED.png)


## Handles `404` for `FILE NOT FOUND` and `500` error for `INTERNAL SERVER ERROR`.</details>
<details>
                <summary>DELETE</summary>
                    <!-- Space -->

## URL: `http://localhost/hot_dog.txt`

Before the DELETE request - we made the `hot_dog.txt` file under `/public` from the `PUT` request section. **Now we are going to delete it**

![DELETE AFTER](markdown/delete_after.png)

## Handles `404` for `FILE NOT FOUND`.

Let's try delete `hot_dog.txt` again after deleting it with the process above.

![DELETE 404](markdown/delete_404.png)</details>
