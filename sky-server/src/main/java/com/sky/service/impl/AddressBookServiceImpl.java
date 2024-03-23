package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Override
    public void insert(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        addressBookMapper.insert(addressBook);
    }

    /**
     * 条件查询
     *
     * @param addressBook
     * @return
     */
    @Override
    public List<AddressBook> list(AddressBook addressBook) {
        List<AddressBook> list = addressBookMapper.list(addressBook);
        return list;
    }

    @Override
    public void updateAddress(AddressBook addressBook) {
        addressBookMapper.update(addressBook);
    }

    @Override
    public void deleteAddress(Long id) {
        addressBookMapper.delete(id);
    }

    @Override
    public AddressBook getAddressById(Long id) {
        AddressBook addressBook = addressBookMapper.getAddressById(id);
        return addressBook;
    }

    /**
     * 设置默认地址
     *
     * @param addressBook
     */
    @Transactional
    public void setDefault(AddressBook addressBook) {
        //1、将当前用户的所有地址修改为非默认地址 update address_book set is_default = ? where user_id = ?
        // 只是借助addressBook这个对象，其实并没有使用里面携带的参数id
        addressBook.setIsDefault(0);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookMapper.updateIsDefaultByUserId(addressBook);

        //2、将当前地址改为默认地址 update address_book set is_default = ? where id = ?
        // 此时使用前端传过来的id，来设置对应的id的地址对象为默认地址
        addressBook.setIsDefault(1);
        addressBookMapper.update(addressBook);
    }
}
