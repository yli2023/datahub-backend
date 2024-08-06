-- 该脚本不要直接执行， 注意维护菜单的父节点ID 默认 父节点-1 , 

-- 菜单SQL
insert into sys_menu ( menu_id,parent_id, path, permission, menu_type, icon, del_flag, create_time, sort_order, update_time, name)
values (1722418294978, '-1', '/demo/project/index', '', '0', 'icon-bangzhushouji', '0', null , '8', null , 'project 表管理');

-- 菜单对应按钮SQL
insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1722418294979,1722418294978, 'demo_project_view', '1', null, '1',  '0', null, '0', null, 'project 表查看');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1722418294980,1722418294978, 'demo_project_add', '1', null, '1',  '0', null, '1', null, 'project 表新增');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon,  del_flag, create_time, sort_order, update_time, name)
values (1722418294981,1722418294978, 'demo_project_edit', '1', null, '1',  '0', null, '2', null, 'project 表修改');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1722418294982,1722418294978, 'demo_project_del', '1', null, '1',  '0', null, '3', null, 'project 表删除');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1722418294983,1722418294978, 'demo_project_export', '1', null, '1',  '0', null, '3', null, '导入导出');