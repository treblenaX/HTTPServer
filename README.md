<h1>HTTP Server</h1>
<p>Please check the individual markdown files or refer to the dropdowns below...</p>
<details>
                <summary>How to Run...</summary>
                    <!-- Space -->

`javac ./*.java ./threads/*.java ; java SocketServer.java` on the root level of `/HTTPServer`</details>
<details>
                <summary>GET</summary>
                    <!-- Space -->

---

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

---

# Supported MIME Types

As the spec mentions, this only supports `text/plain`.

---

## URL: `http://localhost/example.txt`

Before the POST request - `public/example.txt`

    cereal is definitely not a soup

After the POST request - `public/example.txt`

![after POST request](markdown/post_after.png)

---

## (ERROR - 415) URL: `http://localhost/index.html`

![415](markdown/post_415.png)

---

## (ERROR - 404) URL: `http://localhost/index.h`

![404](markdown/post_404.png)</details>
<details>
                <summary>PUT</summary>
                    <!-- Space -->

---

## URL: `http://localhost/example.txt`

Before the PUT request (after the POST request from before)- `public/example.txt`

    cereal is definitely not a soup cereal is a soup >:(

After the PUT request on existing file - `public/example.txt`

![PUT_AFTER](markdown/put_after_OK.png)

After the PUT request on new file - `public/hot_dog.txt`

![Hot Dog](markdown/put_after_CREATED.png)

---

## Handles `404` for `FILE NOT FOUND` and `500` error for `INTERNAL SERVER ERROR`.

    catch (IOException e) {
        outputBytes = (StatusCode.INTERNAL_SERVER_ERROR.toHtml()).getBytes();
        contentType = "text/html";
        code = StatusCode.INTERNAL_SERVER_ERROR;
    } catch (Exception e) {
        outputBytes = (StatusCode.UNSUPPORTED_MEDIA_TYPE.toHtml()).getBytes();
        contentType = "text/html";
        code = StatusCode.UNSUPPORTED_MEDIA_TYPE;
    }
    
    responseHeaders.put("Content-Type", contentType);
    responseHeaders.put("Content-Length", Integer.toString(outputBytes.length));

    LOGGER.info(this.name + " - " + code.toString());
    respond(code, outputBytes, responseHeaders);
</details>
<details>
                <summary>DELETE</summary>
                    <!-- Space -->

---

## URL: `http://localhost/hot_dog.txt`

Before the DELETE request - we made the `hot_dog.txt` file under `/public` from the `PUT` request section. **Now we are going to delete it**

![DELETE AFTER](markdown/delete_after.png)

---

## Handles `404` for `FILE NOT FOUND`.

Let's try delete `hot_dog.txt` again after deleting it with the process above.

![DELETE 404](markdown/delete_404.png)</details>
<details>
                <summary>OPTIONS</summary>
                    <!-- Space -->

---

## Grabbing OPTIONS for text: `http://localhost/example.txt`

![text](markdown/options_text.png)

---

## Grabbing OPTIONS for image: `http://localhost/happy_cat.jpg`

![image](markdown/options_image.png)
</details>
<details>
                <summary>HEAD</summary>
                    <!-- Space -->

---

## URL: `http://localhost/index.html`

After the HEAD request:

![HEAD_INDEX](markdown/head_index.png)

No body is returned.

![HEAD_BODY](markdown/head_body.png)
</details>
<details>
                <summary>Errors as HTTP Cat</summary>
                    <!--- Space -->

Whenever any of these requests hit an Exception or an Error, then an Error response is sent out to the client.

# How the Error HTML is Constructed

    "<html>" + 
        "<head><title>" + this.toString() + "</title></head>" +
        "<body>" +
            "<img src=\"https://http.cat/" + code + "\" alt=\"" + code + "\">" +
        "</body>" +
    "</html>";

Then the server converts that to bytes and then sends it to the client.

---

## (404 - ERROR)

Notice how there is no file with the name `example.tx` within the `/public` folder. Therefore, the server returns an error code of `404 NOT FOUND` along with the corresponding HTTPCat.

![error 404 page](markdown/get_404.png)

## (500 - ERROR) HTML

---

![error 404 page](markdown/cat_500_html.png)

## (500 - ERROR) Preview

---

![error 404 page](markdown/cat_500.png)


</details>
