function showSpinner(button) {

    // disable button
    button.disabled = true;

    // get text
    let text = button.querySelector(".button-text");

    // get spinner
    let spinner = button.querySelector(".spinner-border");

    // change text
    if (text) {
        text.innerText = "Loading...";
    }

    // show spinner
    if (spinner) {
        spinner.classList.remove("d-none");
    }

    // submit form
    button.form.submit();
}