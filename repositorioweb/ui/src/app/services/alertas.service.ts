import {EventEmitter, Injectable} from '@angular/core';
import { Alerta } from '../models/alerta.model';

@Injectable()
export class AlertasService {

  onNuevaAlerta = new EventEmitter<Alerta>();
  constructor() { }

}
