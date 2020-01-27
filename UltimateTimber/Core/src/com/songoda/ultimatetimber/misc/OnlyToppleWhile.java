package com.songoda.ultimatetimber.misc;

public enum OnlyToppleWhile {
    SNEAKING,
    NOT_SNEAKING,
    ALWAYS;

    /**
     * Gets an OnlyToppleWhile from a given string
     *
     * @param string The string
     * @return The TreeAnimationType, returns FANCY if the string is an invalid type
     */
    public static OnlyToppleWhile fromString(String string) {
        for (OnlyToppleWhile value : values())
            if (value.name().equalsIgnoreCase(string))
                return value;
        return OnlyToppleWhile.ALWAYS;
    }
}
