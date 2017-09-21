package com.ken.wms.common.controller;

import com.ken.wms.common.service.Interface.UrlAuthorityService;
import com.ken.wms.common.util.Response;
import com.ken.wms.common.util.ResponseFactory;
import com.ken.wms.domain.UrlAuthorityDTO;
import com.ken.wms.exception.UrlAuthorityServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * URL管理请求 Handler
 * @author 302294马丽丽
 * @since 2017/8/22 18:59
 */
@Controller
@RequestMapping(value = "/**/urlAuthority")
public class UrlAuthorityHandler {
    @Autowired
    private UrlAuthorityService urlAuthorityService;

    // 查询类型
    private static final String SEARCH_BY_ACTION_PARAM = "searchByActionParam";
    private static final String SEARCH_BY_ACTION_NAME = "searchByActionName";
    private static final String SEARCH_ALL = "searchAll";

    private Map<String, Object> query(String keyWord, String searchType, int offset, int limit) throws UrlAuthorityServiceException {
        Map<String, Object> queryResult = null;

        UrlAuthorityDTO authorityDO = new UrlAuthorityDTO();
        //如果页面选择按权限名查询
       if (SEARCH_BY_ACTION_NAME.equals(searchType)) {
           authorityDO.setActionName(keyWord);

           //如果页面选择按权限地址查询
        }else if(SEARCH_BY_ACTION_PARAM.equals(searchType)){
           authorityDO.setActionParam(keyWord);
        }

        queryResult = urlAuthorityService.searchByActionType(offset, limit, authorityDO);
        return queryResult;
    }

    /**
     * 查询仓库管理员信息
     *
     * @param searchType 查询类型
     * @param offset     分页偏移值
     * @param limit      分页大小
     * @param keyWord    查询关键字
     * @return 返回一个Map，其中key=rows，表示查询出来的记录；key=total，表示记录的总条数
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "selectUrlAuthorityList", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> selectUrlAuthorityList(@RequestParam("searchType") String searchType,
                                           @RequestParam("keyWord") String keyWord, @RequestParam("offset") int offset,
                                           @RequestParam("limit") int limit) throws UrlAuthorityServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();

        List<UrlAuthorityDTO> rows = null;
        long total = 0;

        // 查询
        Map<String, Object> queryResult = query(keyWord, searchType, offset, limit);

        if (queryResult != null) {
            rows = (List<UrlAuthorityDTO>) queryResult.get("data");
            total = (long) queryResult.get("total");
        }

        // 设置 Response
        responseContent.setCustomerInfo("rows", rows);
        responseContent.setResponseTotal(total);
        return responseContent.generateResponse();
    }

    /**
     * 删除指定 ID 的菜单权限信息
     * @param urlAuthorityId 菜单权限 ID
     * @return 返回一个map,其中key为result；值为result的值为操作结果，包括：success 和error
     * @throws UrlAuthorityServiceException 异常
     */
    @RequestMapping(value = "deleteUrlAuthorityById", method = RequestMethod.GET)
    public
    @ResponseBody
    Map<String, Object> deleteUrlAuthorityById(Integer urlAuthorityId) throws UrlAuthorityServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();

        // 删除记录
        String result = urlAuthorityService.deleteUrlAuthorityById(urlAuthorityId)
                ? Response.RESPONSE_RESULT_SUCCESS : Response.RESPONSE_RESULT_ERROR;

        // 设置 Response
        responseContent.setResponseResult(result);
        return responseContent.generateResponse();
    }

    /**
     * 新增菜单权限信息
     * @param urlAuthorityDTO 请求实体
     * @return 返回成功/失败
     * @throws UrlAuthorityServiceException 抛异常
     */
    @RequestMapping(value = "addUrlauthorityAction", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> addUrlauthorityAction(@RequestBody UrlAuthorityDTO urlAuthorityDTO) throws UrlAuthorityServiceException {
        //初始化 response
        Response responseContent = ResponseFactory.newInstance();

        //新增一条菜单权限信息
        String result = urlAuthorityService.addUrlauthorityInfo(urlAuthorityDTO)? Response.RESPONSE_RESULT_SUCCESS : Response.RESPONSE_RESULT_ERROR;

        // 设置response
        responseContent.setResponseResult(result);
        return responseContent.generateResponse();
    }

    /**
     * 根据ID修改菜单权限信息
     * @param urlAuthorityDTO  菜单权限信息
     * @return  返回map
     * @throws UrlAuthorityServiceException  抛出异常
     */
    @RequestMapping(value = "updateUrlAuthorityInfo", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, Object> updateUrlAuthorityInfo(@RequestBody UrlAuthorityDTO urlAuthorityDTO) throws UrlAuthorityServiceException {
        //初始化response
        Response responseContent = ResponseFactory.newInstance();

        //修改菜单权限信息
        String result = urlAuthorityService.updateUrlAuthorityInfo(urlAuthorityDTO) ? Response.RESPONSE_RESULT_SUCCESS : Response.RESPONSE_RESULT_ERROR;

        // 设置response
        responseContent.setResponseResult(result);
        return responseContent.generateResponse();
    }


    /**
     * 导出菜单权限信息到文件中
     *
     * @param searchType 查询类型
     * @param keyWord    查询关键字
     * @param response   HttpServletResponse
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "exportUrlAuthority", method = RequestMethod.GET)
    public void exportUrlAuthority(@RequestParam("searchType") String searchType,
                                      @RequestParam("keyWord") String keyWord, HttpServletResponse response) throws UrlAuthorityServiceException, IOException {

        // 导出文件名
        String fileName = "urlAuthorityInfo.xlsx";

        // 查询
        List<UrlAuthorityDTO> urlAuthorityDTOS;
        Map<String, Object> queryResult = query(keyWord, searchType, -1, -1);

        if (queryResult != null)
            urlAuthorityDTOS = (List<UrlAuthorityDTO>) queryResult.get("data");
        else
            urlAuthorityDTOS = new ArrayList<>();

        // 生成文件
        File file = urlAuthorityService.exportUrlAuthority(urlAuthorityDTOS);

        // 输出文件
        if (file != null) {
            // 设置响应头
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
            FileInputStream inputStream = new FileInputStream(file);
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[8192];

            int len;
            while ((len = inputStream.read(buffer, 0, buffer.length)) > 0) {
                outputStream.write(buffer, 0, len);
                outputStream.flush();
            }

            inputStream.close();
            outputStream.close();
        }
    }

    /**
     * 从文件中导入菜单权限信息
     *
     * @param file 保存有菜单权限信息的文件
     * @return 返回一个map，其中：key 为 result表示操作的结果，包括：success 与
     * error；key为total表示导入的总条数；key为available表示有效的条数
     */
    @RequestMapping(value = "importUrlAuthority", method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> importUrlAuthority(MultipartFile file) throws UrlAuthorityServiceException {
        // 初始化 Response
        Response responseContent = ResponseFactory.newInstance();
        String result = Response.RESPONSE_RESULT_ERROR;

        // 读取文件
        long total = 0;
        long available = 0;
        if (file != null) {
            Map<String, Object> importInfo = urlAuthorityService.importUrlAuthority(file);
            if (importInfo != null) {
                total = (long) importInfo.get("total");
                available = (long) importInfo.get("available");
                result = Response.RESPONSE_RESULT_SUCCESS;
            }
        }

        // 设置 Response
        responseContent.setResponseResult(result);
        responseContent.setResponseTotal(total);
        responseContent.setCustomerInfo("available", available);
        return responseContent.generateResponse();
    }
}
