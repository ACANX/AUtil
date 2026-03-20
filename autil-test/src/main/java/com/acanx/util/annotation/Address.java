package com.acanx.util.annotation;

/**
 * Address
 *
 * @author ACANX
 * @since 20260317
 */
public class Address {
    private String street;
    private String city;
    private String zipCode;

    public Address copy() {
        Address copy = new Address();
        copy.street = this.street;
        copy.city = this.city;
        copy.zipCode = this.zipCode;
        return copy;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
}
