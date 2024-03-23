package com.sky.service;

import com.sky.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    void insert(AddressBook addressBook);

    List<AddressBook> list(AddressBook addressBook);

    void updateAddress(AddressBook addressBook);

    void deleteAddress(Long id);

    AddressBook getAddressById(Long id);
    void setDefault(AddressBook addressBook);
}
