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