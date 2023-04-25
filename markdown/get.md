<!-- Space -->

# Supported MIME Types

## `http://localhost/example.txt`
### `text/plain`

![Plain Text Image](./text_plain.png)

## `http://localhost/index.html`
### `text/html`, `image/jpeg`, `image/gif`

Calling the [index HTML page](../public/index.html) using `http://localhost/index.html` and then the HTML page itself is calling the [PNG image](../public/satoru_gojo.jpg) and the [GIF image](../public/jujutsu-kaisen-funny.gif) inside the HTML.

    <div id="img_container">
        <!-- HTML Q4 -->
        <img src="./satoru_gojo.jpg" alt="Main image of Satoru Gojo"/>
        <div id="img_padding"></div>
        <img src="./jujutsu-kaisen-funny.gif" alt="Funny GIF of Satoru Gojo"/>
    </div>

>The `./satoru_gojo.jpg` and `jujutsu-kaisen-funny.gif` are relative paths that is in the `/public` folder and on the same level as the `index.html`.

![Index HTML image](./index_html.png)