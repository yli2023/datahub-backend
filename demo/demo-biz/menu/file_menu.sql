-- 该脚本不要直接执行， 注意维护菜单的父节点ID 默认 父节点-1 , 

-- 菜单SQL
insert into sys_menu ( menu_id,parent_id, path, permission, menu_type, icon, del_flag, create_time, sort_order, update_time, name)
values (1722414585594, '-1', '/demo/file/index', '', '0', 'icon-bangzhushouji', '0', null , '8', null , 'file 表管理');

-- 菜单对应按钮SQL
insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1722414585595,1722414585594, 'demo_file_view', '1', null, '1',  '0', null, '0', null, 'file 表查看');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1722414585596,1722414585594, 'demo_file_add', '1', null, '1',  '0', null, '1', null, 'file 表新增');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon,  del_flag, create_time, sort_order, update_time, name)
values (1722414585597,1722414585594, 'demo_file_edit', '1', null, '1',  '0', null, '2', null, 'file 表修改');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1722414585598,1722414585594, 'demo_file_del', '1', null, '1',  '0', null, '3', null, 'file 表删除');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1722414585599,1722414585594, 'demo_file_export', '1', null, '1',  '0', null, '3', null, '导入导出');