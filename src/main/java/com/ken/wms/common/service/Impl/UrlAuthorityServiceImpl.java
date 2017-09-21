package com.ken.wms.common.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ken.wms.common.service.Interface.UrlAuthorityService;
import com.ken.wms.common.util.EJConvertor;
import com.ken.wms.common.util.FileUtil;
import com.ken.wms.dao.UrlAuthorityMapper;
import com.ken.wms.domain.UrlAuthorityDTO;
import com.ken.wms.exception.UrlAuthorityServiceException;
import com.ken.wms.util.aop.UserOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 302294马丽丽
 * @since 2017/8/22 19:24
 */
@Service
public class UrlAuthorityServiceImpl implements UrlAuthorityService {

    @Autowired
    private UrlAuthorityMapper urlAuthorityMapper;
    @Autowired
    private EJConvertor ejConvertor;

    /**
     * 根据 传入的参数  返回指定菜单权限信息 支持模糊查询
     * @param offset  页面条数
     * @param limit 页面条数
     * @param authorityDO 传入实体
     * @return 返回结果list
     */
    @Override
    public Map<String, Object> searchByActionType(int offset, int limit, UrlAuthorityDTO authorityDO) {
        // 初始化结果集
        Map<String, Object> resultSet = new HashMap<>();
        List<UrlAuthorityDTO> urlAuthorities;
        long total = 0;
        boolean isPagination = true;

        // validate
        if (offset < 0 || limit < 0)
            isPagination = false;

        // query
        if (isPagination) {
            PageHelper.offsetPage(offset, limit);
            urlAuthorities = urlAuthorityMapper.searchByActionType(authorityDO);
            if (urlAuthorities != null) {
                PageInfo<UrlAuthorityDTO> pageInfo = new PageInfo<>(urlAuthorities);
                total = pageInfo.getTotal();
            } else
                urlAuthorities = new ArrayList<>();
        } else {
            urlAuthorities = urlAuthorityMapper.searchByActionType(authorityDO);
            if (urlAuthorities != null){
                total = urlAuthorities.size();
            }
            else{
                urlAuthorities = new ArrayList<>();
            }
        }

        resultSet.put("data", urlAuthorities);
        resultSet.put("total", total);
        return resultSet;
    }

    /**
     * 删除菜单权限信息
     *
     * @param urlAuthorityId ID
     * @return 返回一个boolean值，值为true代表删除成功，否则代表失败
     */
    @UserOperation(value = "删除菜单权限信息")
    @Override
    public boolean deleteUrlAuthorityById(Integer urlAuthorityId) throws UrlAuthorityServiceException {

        try {
            int count = urlAuthorityMapper.deleteUrlAuthorityById(urlAuthorityId);
            if (count > 0){
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            throw new UrlAuthorityServiceException(e);
        }
    }

    /**
     * 添加菜单权限信息
     * @param urlAuthorityDTO  页面传入的参数实体
     * @return  返回 布尔值 成功/失败
     * @throws UrlAuthorityServiceException 抛出异常
     */
    @UserOperation(value = "添加菜单权限信息")
    @Override
    public boolean addUrlauthorityInfo(UrlAuthorityDTO urlAuthorityDTO) throws UrlAuthorityServiceException {
        try {
            //如果传入的请求名和地址不为空则新增
            if (urlAuthorityDTO != null && StringUtils.isNotBlank(urlAuthorityDTO.getActionName())
                    && StringUtils.isNotBlank(urlAuthorityDTO.getActionParam())) {
                //调用新增方法
                urlAuthorityMapper.addUrlauthorityInfo(urlAuthorityDTO);
                return true;
            }else{
                //否则抛异常
                //throw new UrlAuthorityServiceException(UrlAuthorityServiceException.ENTITY_IS_NULL);
                return false;
            }

        } catch (Exception e) {
            throw new UrlAuthorityServiceException(e,UrlAuthorityServiceException.ENTITY_IS_NULL);
        }
    }

    @UserOperation(value = "修改菜单权限信息")
    @Override
    public boolean updateUrlAuthorityInfo(UrlAuthorityDTO urlAuthorityDTO) throws UrlAuthorityServiceException{
        try {
            if (urlAuthorityDTO != null){
                if (StringUtils.isNotBlank(urlAuthorityDTO.getActionName())
                        && StringUtils.isNotBlank(urlAuthorityDTO.getActionParam())) {
                    //调用修改方法
                    urlAuthorityMapper.updateUrlAuthorityInfo(urlAuthorityDTO);
                    return true;
                } else {
                    return false;
                }
            }else{
                return false;
                //throw  new UrlAuthorityServiceException(UrlAuthorityServiceException.ENTITY_IS_NULL);
            }
        }catch (Exception e){
            throw new UrlAuthorityServiceException(e,UrlAuthorityServiceException.ENTITY_IS_NULL);
        }
    }

    /**
     * 导出菜单权限信息到文件中
     *
     * @param urlAuthorityDTOList 包含若干条 repository 信息的 List
     * @return Excel 文件
     */
    @UserOperation(value = "导出菜单权限信息")
    @Override
    public File exportUrlAuthority(List<UrlAuthorityDTO> urlAuthorityDTOList) {
        File file = null;

        if (urlAuthorityDTOList != null) {
            file = ejConvertor.excelWriter(UrlAuthorityDTO.class, urlAuthorityDTOList);
        }

        return file;
    }

    /**
     * 从文件中导入菜单权限信息
     *
     * @param file 导入信息的文件
     * @return 返回一个Map，其中：key为total代表导入的总记录数，key为available代表有效导入的记录数
     */
    @UserOperation(value = "导入菜单权限信息")
    @Override
    public Map<String, Object> importUrlAuthority(MultipartFile file) throws UrlAuthorityServiceException {
        // 初始化结果集
        Map<String, Object> resultSet = new HashMap<>();
        long total = 0;
        long available = 0;

        // 从文件中读取
        try {
            List<UrlAuthorityDTO> urlAuthorityDTOS = ejConvertor.excelReader(UrlAuthorityDTO.class, FileUtil.convertMultipartFileToFile(file));

            if (urlAuthorityDTOS != null) {
                total = urlAuthorityDTOS.size();

                // 验证记录
                List<UrlAuthorityDTO> availableList = new ArrayList<>();
                for (UrlAuthorityDTO urlAuthorityDTO : urlAuthorityDTOS) {
                    if (StringUtils.isNotBlank(urlAuthorityDTO.getActionName())
                            && StringUtils.isNotBlank(urlAuthorityDTO.getActionParam())
                            && StringUtils.isNotBlank(urlAuthorityDTO.getActionDesc())){
                        availableList.add(urlAuthorityDTO);
                    }

                }

                // 保存到数据库
                available = availableList.size();
                if (available > 0)
                    urlAuthorityMapper.insertBatch(availableList);
            }
        } catch (PersistenceException | IOException e) {
            throw new UrlAuthorityServiceException(e);
        }

        resultSet.put("total", total);
        resultSet.put("available", available);
        return resultSet;
    }
}
