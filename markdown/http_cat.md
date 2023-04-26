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


