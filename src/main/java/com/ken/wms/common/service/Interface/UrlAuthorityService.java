package com.ken.wms.common.service.Interface;

import com.ken.wms.domain.UrlAuthorityDTO;
import com.ken.wms.exception.UrlAuthorityServiceException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @author 302294马丽丽
 * @since 2017/8/22 19:06
 */

public interface UrlAuthorityService {

    /**
     * 根据请求名查询URL权限信息
     * @param offset
     * @param limit
     * @param authorityDO
     * @return
     */
    public Map<String, Object> searchByActionType(int offset, int limit, UrlAuthorityDTO authorityDO);

    /**
     * 根据ID删除菜单权限信息
     * @param urlAuthorityId 传入的id
     * @return true/false
     * @throws UrlAuthorityServiceException  抛出异常
     */
    public boolean deleteUrlAuthorityById(Integer urlAuthorityId) throws UrlAuthorityServiceException;

    /**
     * 新增菜单权限信息
     * @param urlAuthorityDTO  页面传入的参数实体
     * @return  返回 布尔值 成功/失败
     * @throws UrlAuthorityServiceException  抛出异常
     */
    public boolean addUrlauthorityInfo(UrlAuthorityDTO urlAuthorityDTO) throws UrlAuthorityServiceException;

    /**
     * 修改菜单权限信息
     * @param urlAuthorityDTO
     * @return
     * @throws UrlAuthorityServiceException
     */
    public boolean updateUrlAuthorityInfo(UrlAuthorityDTO urlAuthorityDTO) throws UrlAuthorityServiceException;

    /**
     * 导出仓菜单权限信息到文件中
     *
     * @param urlAuthorityDTOList 包含若干条 repository 信息的 List
     * @return Excel 文件
     */
    File exportUrlAuthority(List<UrlAuthorityDTO> urlAuthorityDTOList);

    /**
     * 从文件中导入菜单权限信息
     * @param file
     * @return
     * @throws UrlAuthorityServiceException
     */
    public Map<String, Object> importUrlAuthority(MultipartFile file) throws UrlAuthorityServiceException;
}
