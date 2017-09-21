package com.ken.wms.dao;

import com.ken.wms.domain.UrlAuthority;
import com.ken.wms.domain.UrlAuthorityDTO;

import java.util.List;

/**
 * RepositoryAdmin 映射器
 * @author Ken
 *
 */
public interface UrlAuthorityMapper {

	/**
	 * 选择所有的URL权限
	 * @return 返回所有的URL权限信息
	 */
	List<UrlAuthority> selectAll();

	/**
	 * 根据请求名返回菜单权限信息
	 * @param authorityDO 传入实体
	 * @return 返回list
	 */
	List<UrlAuthorityDTO> searchByActionType(UrlAuthorityDTO authorityDO);

	/**
	 * 根据ID删除菜单权限信息
	 * @param id  ID
	 * @return 返回int值
	 */
	public int deleteUrlAuthorityById(Integer id);

	/**
	 * 新增菜单权限信息
	 * @param urlAuthorityDTO  传入实体
	 */
	void addUrlauthorityInfo(UrlAuthorityDTO urlAuthorityDTO);

	/**
	 * 修改菜单权限信息
	 * @param urlAuthorityDTO
	 */
	void updateUrlAuthorityInfo(UrlAuthorityDTO urlAuthorityDTO);

	/**
	 * 批量插入菜单权限信息
	 * @param urlAuthorityDTOList 存放若干条菜单权限信息的 List
	 */
	void insertBatch(List<UrlAuthorityDTO> urlAuthorityDTOList);
}
