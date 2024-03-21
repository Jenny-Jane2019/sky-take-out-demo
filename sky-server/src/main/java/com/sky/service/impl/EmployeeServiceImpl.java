package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // 需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println("加密后的密码为：" + password);
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public Employee save(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        // 对象属性拷贝(两个参数，source, target)
        BeanUtils.copyProperties(employeeDTO, employee);
        // 设置账号状态（新建时默认时ENABLE）
        employee.setStatus(StatusConstant.ENABLE);
        // 设置当前记录的创建时间喝修改时间（AOP实现）
        // employee.setCreateTime(LocalDateTime.now());
        // employee.setUpdateTime(LocalDateTime.now());
        // 设置密码，默认值123456
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        // 设置当前记录创建人id和修改人id（AOP实现）
        // 在Service中获取线程局部变量中的值
        // Long empId = BaseContext.getCurrentId();
        // employee.setCreateUser(empId);
        // employee.setUpdateUser(empId);
        employeeMapper.insert(employee);
        return employee;
    }

    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // 分页查询 使用Mybatis的分页插件
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        // 接下来执行的查询操作会被 PageHelper 拦截并进行分页处理, 会自动在原始的查询语句中添加分页的 SQL 语句，例如在 MySQL 中是使用 LIMIT 关键字来实现分页。这样就保证了查询结果只返回符合分页条件的部分数据，而不是全部数据。
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        // 将结果写入
        PageResult pageResult = new PageResult();
        pageResult.setTotal(page.getTotal());
        pageResult.setRecords(page.getResult());
        return pageResult;
    }

    /**
     * 启用禁用员工账号
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        //建造者模式（Builder Pattern）来创建一个 Employee 对象
        // 并设置了对象的 status 和 id 属性。
        Employee employee = Employee.builder().status(status).id(id).build();
        employeeMapper.update(employee);
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.getById(id);
        if(employee != null){
            employee.setPassword("********");
        }
        return employee;
    }

    /**
     * 编辑员工信息
     * @param employeeDTO
     * @return
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        // 需要更新员工表中的更新人id和更新时间字段（AOP实现）
        // employee.setUpdateUser(BaseContext.getCurrentId());
        // employee.setUpdateTime(LocalDateTime.now());
        employeeMapper.update(employee);
    }
}
