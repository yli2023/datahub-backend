-- 该脚本不要直接执行， 注意维护菜单的父节点ID 默认 父节点-1 , 

-- 菜单SQL
insert into sys_menu ( menu_id,parent_id, path, permission, menu_type, icon, del_flag, create_time, sort_order, update_time, name)
values (1722819444410, '-1', '/demo/control/index', '', '0', 'icon-bangzhushouji', '0', null , '8', null , 'control 表管理');

-- 菜单对应按钮SQL
insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1722819444411,1722819444410, 'demo_control_view', '1', null, '1',  '0', null, '0', null, 'control 表查看');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1722819444412,1722819444410, 'demo_control_add', '1', null, '1',  '0', null, '1', null, 'control 表新增');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon,  del_flag, create_time, sort_order, update_time, name)
values (1722819444413,1722819444410, 'demo_control_edit', '1', null, '1',  '0', null, '2', null, 'control 表修改');

insert into sys_menu (menu_id, parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1722819444414,1722819444410, 'demo_control_del', '1', null, '1',  '0', null, '3', null, 'control 表删除');

insert into sys_menu ( menu_id,parent_id, permission, menu_type, path, icon, del_flag, create_time, sort_order, update_time, name)
values (1722819444415,1722819444410, 'demo_control_export', '1', null, '1',  '0', null, '3', null, '导入导出');