#!/usr/bin/python3

from flask import Flask, redirect, request

app = Flask(__name__)

@app.route("/redirect")
def redirect_by_url():
    target_url = request.args.get("url")

    if target_url:
        return redirect(target_url)
    else:
        return "No URL for redirection", 400

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=10071)
