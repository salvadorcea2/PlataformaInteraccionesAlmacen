alter table servicio add column tipo_tramite_id INTEGER;
alter table servicio add constraint fk_servicio_tipo_tramote FOREIGN KEY (tipo_tramite_id) references tipo_tramite(id);

drop table dwh.transaccion_interoperabilidad;
create table dwh.transaccion_interoperabilidad  (
  id serial not null constraint pk_log primary key,
  fecha_creacion timestamp with time zone DEFAULT now(),
  fecha_log timestamp with time zone,
  host_remoto varchar(20),
  servicio varchar(100),
  metodo_http varchar(20),
  uri varchar(200),
  status_http integer,
  codigo_institucion_consumidora varchar(5),
  id_transaccion varchar(36),
  servicio_id integer not null,
  institucion_id integer not null,
  constraint fk_transaccion_interoperabilidad_servicio foreign key (servicio_id) references servicio(id),
  constraint fk_transaccion_interoperabilidad_institucion foreign key (institucion_id) references institucion(id)
);

﻿alter table tipo_tramite add column tiempo_espera integer;


