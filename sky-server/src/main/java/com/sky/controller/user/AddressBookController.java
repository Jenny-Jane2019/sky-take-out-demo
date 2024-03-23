package com.sky.controller.user;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
@Api(tags="地址相关接口")
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;
    @PostMapping
    @ApiOperation("新增地址")
    public Result addAddress(@RequestBody AddressBook addressBook){
        log.info("新增地址：{}", addressBook);
        addressBookService.insert(addressBook);
        return Result.success();
    }
    @GetMapping("list")
    @ApiOperation("查询用户所有地址")
    public Result<List<AddressBook>> list(){
        AddressBook addressBook = AddressBook.builder().userId(BaseContext.getCurrentId()).build();
        List<AddressBook> list = addressBookService.list(addressBook);
        return Result.success(list);
    }

    @GetMapping("/default")
    @ApiOperation("查看默认地址")
    public Result<AddressBook> getDefaultAddress(){
        AddressBook addressBook = AddressBook.builder().userId(BaseContext.getCurrentId()).isDefault(1).build();
        List<AddressBook> list = addressBookService.list(addressBook);
        if(list!=null && list.size() ==1){
            return Result.success(list.get(0));
        }
        return Result.error("没有查询到默认地址");
    }

    /**
     * 根据id修改地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping
    @ApiOperation("修改地址")
    public Result updateAddress(@RequestBody AddressBook addressBook){
        addressBookService.updateAddress(addressBook);
        return Result.success();
    }

    /**
     * 根据id删除地址
     *
     * @param id
     * @return
     */
    @DeleteMapping
    @ApiOperation("根据id删除地址")
    public Result deleteAddress(Long id){
        addressBookService.deleteAddress(id);
        return Result.success();
    }

    /**
     * 根据id查询地址
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址")
    public Result<AddressBook> getAddressById(@PathVariable Long id){
        AddressBook addressBook = addressBookService.getAddressById(id);
        return Result.success(addressBook);
    }

    /**
     * 设置默认地址
     *
     * @param addressBook(只有需要设置默认地址的addressBook.id)
     * @return
     */
    @PutMapping("/default")
    @ApiOperation("设置默认地址")
    public Result setDefaultAddress(@RequestBody AddressBook addressBook){
        log.info("设置默认地址：{}", addressBook);
        addressBookService.setDefault(addressBook);
        return Result.success();
    }
}
