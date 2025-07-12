package com.respiroc.address.domain.model;


import jakarta.persistence.*;

@Entity
@Table(name = "addresses")
class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    var id: Long = -1

    @Column(name = "country_iso_code", nullable = false, length = 2)
    var countryIsoCode: String = "NO"

    @Column(name = "administrative_division_code")
    var administrativeDivisionCode: String? = null

    @Column(name = "city", nullable = false)
    var city: String = ""

    @Column(name = "postal_code")
    var postalCode: String? = null

    @Column(name = "street_address_1", nullable = false)
    var primaryAddress: String = ""

    @Column(name = "street_address_2")
    var secondaryAddress: String? = null
}