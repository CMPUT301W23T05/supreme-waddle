package com.example.qrky.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer passwordError;
    private boolean isDataValid;

    LoginFormState(@Nullable Integer usernameError, @Nullable Integer passwordError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.usernameError = null;
        this.passwordError = null;
        this.isDataValid = isDataValid;
    }

    /**
     * Returns username format errors.
     * @return username format errors.
     */
    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    /**
     * Returns password errors.
     * @return password errors.
     */
    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    /**
     * Checks if the data in the form is valid.
     * @return true if the data in the form is valid.
     */
    boolean isDataValid() {
        return isDataValid;
    }
}