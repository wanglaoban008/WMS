
var search_type_urlAuthority = "none";
var search_keyWord = "";
var selectID;

$(function() {
    optionAction();
    searchAction();
    urlAuthorityListInit();
    bootstrapValidatorInit();
    datePickerInit();

    addUrlauthorityAction();
    editUrlauthorityAction();
    deleteUrlAuthorityAction();
    importUrlAuthority();
    exportUrlAuthorityAction()
})

// 下拉框選擇動作
function optionAction() {
    $(".dropOption").click(function() {
        var type = $(this).text();
        $("#search_input").val("");
        if (type == "所有") {
            $("#search_input").attr("readOnly", "true");
            search_type_urlAuthority = "searchAll";
        } else if (type == "请求名") {
            $("#search_input").removeAttr("readOnly");
            search_type_urlAuthority = "searchByActionName";
        } else if(type == "请求地址"){
            $("#search_input").removeAttr("readOnly");
            search_type_urlAuthority = "searchByActionParam";
        } else {
            $("#search_input").removeAttr("readOnly");
        }

        $("#search_type").text(type);
        $("#search_input").attr("placeholder", type);
    })
}

// 搜索动作
function searchAction() {
    $('#search_button').click(function() {
        search_keyWord = $('#search_input').val();
        tableRefresh();
    })
}

// 分页查询参数
function queryParams(params) {
    var temp = {
        limit : params.limit,
        offset : params.offset,
        searchType : search_type_urlAuthority,
        keyWord : search_keyWord
    }
    return temp;
}

// 表格初始化
function urlAuthorityListInit() {
    $('#urlAuthorityList')
        .bootstrapTable(
            {
                columns : [
                    {
                        field : 'id',
                        title : 'URL权限ID'
                        //sortable: true
                    },
                    {
                        field : 'actionName',
                        title : '请求名称'
                    },
                    {
                        field : 'actionDesc',
                        title : '请求描述'
                    },
                    {
                        field : 'actionParam',
                        title : '请求地址'
                    },
                    {
                        field : 'operation',
                        title : '操作',
                        formatter : function(value, row, index) {
                            var s = '<button class="btn btn-info btn-sm edit"><span>编辑</span></button>';
                            var d = '<button class="btn btn-danger btn-sm delete"><span>删除</span></button>';
                            var fun = '';
                            return s + ' ' + d;
                        },
                        events : {
                            // 操作列中编辑按钮的动作
                            'click .edit' : function(e, value,
                                                     row, index) {
                                selectID = row.id;
												rowEditOperation(row);
                            },
                            'click .delete' : function(e,
                                                       value, row, index) {
                                selectID = row.id;
												$('#deleteWarning_modal').modal(
														'show');
                            }
                        }
                    } ],
                url : 'urlAuthority/selectUrlAuthorityList',
                onLoadError:function(status){
                    handleAjaxError(status);
                },
                method : 'GET',
                queryParams : queryParams,
                sidePagination : "server",
                dataType : 'json',
                pagination : true,
                pageNumber : 1,
                pageSize : 5,
                pageList : [ 5, 10, 25, 50, 100 ],
                clickToSelect : true
            });
}

// 表格刷新
function tableRefresh() {
    $('#urlAuthorityList').bootstrapTable('refresh', {
        query : {}
    });
}

// 行编辑操作
var unassignRepoCache;
function rowEditOperation(row) {
    $('#edit_modal').modal("show");

    // load info
    $('#urlAuthority_form_edit').bootstrapValidator("resetForm", true);
    $('#urlauthority_name_edit').val(row.actionName);
    $('#urlauthority_desc_edit').val(row.actionDesc);
    $('#urlauthority_param_edit').val(row.actionParam);

}

// 日期选择器初始化
function datePickerInit(){
    $('.form_date').datetimepicker({
        format:'yyyy-mm-dd',
        language : 'zh-CN',
        endDate : new Date(),
        weekStart : 1,
        todayBtn : 1,
        autoClose : 1,
        todayHighlight : 1,
        startView : 2,
        forceParse : 0,
        minView:2
    });
}

// 添加供应商模态框数据校验
function bootstrapValidatorInit() {
    $("#urlAuthority_form,#urlAuthority_form_edit").bootstrapValidator({
        message : 'This is not valid',
        feedbackIcons : {
            valid : 'glyphicon glyphicon-ok',
            invalid : 'glyphicon glyphicon-remove',
            validating : 'glyphicon glyphicon-refresh'
        },
        excluded : [ ':disabled' ],
        fields : {
            repositoryAdmin_name : {
                validators : {
                    notEmpty : {
                        message : '仓库管理员姓名不能为空'
                    }
                }
            },
            repositoryAdmin_tel : {
                validators : {
                    notEmpty : {
                        message : '仓库管理员联系电话不能为空'
                    }
                }
            },
            repositoryAdmin_address : {
                validators : {
                    notEmpty : {
                        message : '仓库管理员联系地址不能为空'
                    }
                }
            },
            repositoryAdmin_birth : {
                validators : {
                    notEmpty : {
                        message : '仓库管理员出身日期不能为空'
                    }
                }
            }

        }
    })
}

// 编辑仓库管理员信息
function editUrlauthorityAction() {
    $('#edit_modal_submit').click(
        function() {
            $('#urlAuthority_form_edit').data('bootstrapValidator')
                .validate();
            if (!$('#urlAuthority_form_edit').data('bootstrapValidator')
                    .isValid()) {
                return;
            }

            var data = {
                id : selectID,
                actionName : $('#urlauthority_name_edit').val(),
                actionDesc : $('#urlauthority_desc_edit').val(),
                actionParam : $('#urlauthority_param_edit').val()
            }

            // ajax
            $.ajax({
                type : "POST",
                url : 'urlAuthority/updateUrlAuthorityInfo',
                dataType : "json",
                contentType : "application/json",
                data : JSON.stringify(data),
                success : function(response) {
                    $('#edit_modal').modal("hide");
                    var type;
                    var msg;
                    var append = '';
                    if (response.result == "success") {
                        type = "success";
                        msg = "菜单权限信息更新成功";
                    } else if (response.result == "error") {
                        type = "error";
                        msg = "菜单权限信息更新失败"
                    }
                    showMsg(type, msg, append);
                    tableRefresh();
                },
                error : function(xhr, textStatus, errorThown) {
                    $('#edit_modal').modal("hide");
                    // handler error
                    handleAjaxError(xhr.status);
                }
            });
        });

}

// 刪除仓库管理员信息
function deleteUrlAuthorityAction(){
    $('#delete_confirm').click(function(){
        var data = {
            "urlAuthorityId" : selectID
        }

        // ajax
        $.ajax({
            type : "GET",
            url : "urlAuthority/deleteUrlAuthorityById",
            dataType : "json",
            contentType : "application/json",
            data : data,
            success : function(response){
                $('#deleteWarning_modal').modal("hide");
                var type;
                var msg;
                var append = '';
                if(response.result == "success"){
                    type = "success";
                    msg = "菜单权限信息删除成功";
                }else{
                    type = "error";
                    msg = "菜单权限信息删除失败";
                }
                showMsg(type, msg, append);
                tableRefresh();
            },error : function(xhr, textStatus, errorThown){
                $('#deleteWarning_modal').modal("hide");
                // handler error
                handleAjaxError(xhr.status);
            }
        })

        $('#deleteWarning_modal').modal('hide');
    })
}

// 添加菜单权限信息
//	function addUrlauthorityAction() {
//		$('#add_urlauthority').click(function() {
//			$('#add_modal').modal("show");
//		});
//
//		$('#add_modal_submit').click(function() {
//
//            $('#urlAuthority_form').bootstrapValidator({
//                message : 'This value is not valid',
//                feedbackIcons : {
//                    valid : 'glyphicon glyphicon-ok',
//                    invalid : 'glyphicon glyphicon-remove',
//                    validating : 'glyphicon glyphicon-refresh'
//                },
//                fields : {
//                    urlAuthority_name : {
//                        validators : {
//                            notEmpty : {
//                                message : '请求名不能为空'
//                            },
//                            callback : {}
//                        }
//                    },
//                    urlAuthority_param : {
//                        validators : {
//                            notEmpty : {
//                                message : '请求地址不能为空'
//                            },
//                            callback : {}
//                        }
//                    }
//                }
//            })
//                .on('success.form.bv', function(e) {
//                    // 禁用默认表单提交
//                    e.preventDefault();
//
//                    // 获取 form 实例
//                    var $form = $(e.target);
//                    // 获取 bootstrapValidator 实例
//                    var bv = $form.data('bootstrapValidator');
//
//                    // 发送数据到后端 进行验证
//                    var data = {
//                        actionName : $('#urlauthority_name').val(),
//                        actionDesc : $('#urlauthority_desc').val(),
//                        actionParam : $('#urlauthority_url').val()
//                    }
//
//                    // ajax
//                    $.ajax({
//                        type : "POST",
//                        url : "urlAuthority/addUrlauthorityAction",
//                        dataType : "json",
//                        contentType : "application/json",
//                        data : JSON.stringify(data),
//                        success : function(response) {
//                            $('#add_modal').modal("hide");
//                            var msg;
//                            var type;
//                            var append = '';
//                            if (response.result == "success") {
//                                type = "success";
//                                msg = "菜单权限添加成功";
////						append = '注意：仓库管理员的系统初始密码为该ID';
//                            } else if (response.result == "error") {
//                                if(response.msg == "urlAuthorityDTOIsNull"){
//                                    type = "error";
//                                    msg = "传入的实体为空，新增失败";
//                                }else{
//                                    type = "error";
//                                    msg = "菜单权限添加失败";
//								}
//                            }
//                            showMsg(type, msg, append);
//                            tableRefresh();
//
//                            // reset
//                            $('#urlauthority_name').val("");
//                            $('#urlauthority_desc').val("");
//                            $('#urlauthority_url').val("");
//                            $('#urlAuthority_form').bootstrapValidator("resetForm", true);
//                        },
//                        error : function(xhr, textStatus, errorThown) {
//                            $('#add_modal').modal("hide");
//                            // handler error
//                            handleAjaxError(xhr.status);
//                        }
//                    })
//
//                });
//		});
//	}


// 添加仓库管理员信息
function addUrlauthorityAction() {
    $('#add_urlauthority').click(function() {
        $('#add_modal').modal("show");
    });

    $('#add_modal_submit').click(function() {
        var data = {
            actionName : $('#urlauthority_name').val(),
            actionDesc : $('#urlauthority_desc').val(),
            actionParam : $('#urlauthority_url').val()
        }
        // ajax
        $.ajax({
            type : "POST",
            url : "urlAuthority/addUrlauthorityAction",
            dataType : "json",
            contentType : "application/json",
            data : JSON.stringify(data),
            success : function(response) {
                $('#add_modal').modal("hide");
                var msg;
                var type;
                var append = '';
                if (response.result == "success") {
                    type = "success";
                    msg = "菜单权限添加成功";
//                        append = '注意：仓库管理员的系统初始密码为该ID';
                } else if (response.result == "error") {
                    if(response.message == "urlAuthorityDTOIsNull"){
                        type = "error";
                        msg = "传入的实体为空，新增失败";
                    }else{
                        type = "error";
                        msg = "菜单权限添加失败";
                    }

                }
                showMsg(type, msg, append);
                tableRefresh();

                // reset
                $('#urlauthority_name').val("");
                $('#urlauthority_desc').val("");
                $('#urlauthority_url').val("");
                $('#urlAuthority_form').bootstrapValidator("resetForm", true);
            },
            error : function(xhr, textStatus, errorThown) {
                $('#add_modal').modal("hide");
                // handler error
                handleAjaxError(xhr.status);
            }
        })
    })
}

var import_step = 1;
var import_start = 1;
var import_end = 3;
// 导入菜单权限信息
function importUrlAuthority() {
    $('#import_urlAuthority').click(function() {
        $('#import_modal').modal("show");
    });

    $('#previous').click(function() {
        if (import_step > import_start) {
            var preID = "step" + (import_step - 1)
            var nowID = "step" + import_step;

            $('#' + nowID).addClass("hide");
            $('#' + preID).removeClass("hide");
            import_step--;
        }
    })

    $('#next').click(function() {
        if (import_step < import_end) {
            var nowID = "step" + import_step;
            var nextID = "step" + (import_step + 1);

            $('#' + nowID).addClass("hide");
            $('#' + nextID).removeClass("hide");
            import_step++;
        }
    })

    $('#file').on("change", function() {
        $('#previous').addClass("hide");
        $('#next').addClass("hide");
        $('#submit').removeClass("hide");
    })

    $('#submit').click(function() {
        var nowID = "step" + import_end;
        $('#' + nowID).addClass("hide");
        $('#uploading').removeClass("hide");

        // next
        $('#confirm').removeClass("hide");
        $('#submit').addClass("hide");

        // ajax
        $.ajaxFileUpload({
            url : "urlAuthority/importUrlAuthority",
            secureuri: false,
            dataType: 'json',
            fileElementId:"file",
            success : function(data, status){
                var total = 0;
                var available = 0;
                var msg1 = "菜单权限信息导入成功";
                var msg2 = "菜单权限信息导入失败";
                var info;

                $('#import_progress_bar').addClass("hide");
                if(data.result == "success"){
                    total = data.total;
                    available = data.available;
                    info = msg1;
                    $('#import_success').removeClass('hide');
                }else{
                    info = msg2
                    $('#import_error').removeClass('hide');
                }
                info = info + ",总条数：" + total + ",有效条数:" + available;
                $('#import_result').removeClass('hide');
                $('#import_info').text(info);
                $('#confirm').removeClass('disabled');
            },error : function(data, status){
                // handler error
                handleAjaxError(status);
            }
        })
    })

    $('#confirm').click(function() {
        // modal dissmiss
        importModalReset();
    })
}

// 导出菜单权限信息
function exportUrlAuthorityAction() {
    $('#export_urlAuthority').click(function() {
        $('#export_modal').modal("show");
    })

    $('#export_urlAuthority_download').click(function(){
        var data = {
            searchType : search_type_urlAuthority,
            keyWord : search_keyWord
        }
        var url = "urlAuthority/exportUrlAuthority?" + $.param(data)
        window.open(url, '_blank');
        $('#export_modal').modal("hide");
    })
}

// 导入菜单权限模态框重置
function importModalReset(){
    var i;
    for(i = import_start; i <= import_end; i++){
        var step = "step" + i;
        $('#' + step).removeClass("hide")
    }
    for(i = import_start; i <= import_end; i++){
        var step = "step" + i;
        $('#' + step).addClass("hide")
    }
    $('#step' + import_start).removeClass("hide");

    $('#import_progress_bar').removeClass("hide");
    $('#import_result').removeClass("hide");
    $('#import_success').removeClass('hide');
    $('#import_error').removeClass('hide');
    $('#import_progress_bar').addClass("hide");
    $('#import_result').addClass("hide");
    $('#import_success').addClass('hide');
    $('#import_error').addClass('hide');
    $('#import_info').text("");
    $('#file').val("");

    $('#previous').removeClass("hide");
    $('#next').removeClass("hide");
    $('#submit').removeClass("hide");
    $('#confirm').removeClass("hide");
    $('#submit').addClass("hide");
    $('#confirm').addClass("hide");

    $('#file').on("change", function() {
        $('#previous').addClass("hide");
        $('#next').addClass("hide");
        $('#submit').removeClass("hide");
    })

    import_step = 1;
}

