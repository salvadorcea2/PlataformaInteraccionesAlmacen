import {Component, Input, OnInit} from '@angular/core';
import { NG_VALUE_ACCESSOR, ControlValueAccessor } from '@angular/forms';
import { noop } from 'rxjs/util/noop';
import { ApiService } from '../../services/api.service';

@Component({
  selector: 'app-seleccionable',
  template: `
      <label *ngIf="label"><ng-content></ng-content></label>
      <select placeholder="placeholder" [(ngModel)]="value" (blur)="onBlur()">
        <option disabled selected value>{{placeholder}}</option>
        <option *ngFor="let item of lista" [value]="item.id">{{ item.nombre }}</option>
      </select>
  `,
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: SeleccionableComponent,
      multi: true,
    }
  ]
})
export class SeleccionableComponent implements ControlValueAccessor, OnInit {
  @Input() entidad: string;
  @Input() label: boolean;
  @Input() placeholder: string;

  private innerValue: any = '';
  public lista: any[] = [];

  private onTouchedCallback: () => void = noop;
  private onChangeCallback: (_: any) => void = noop;

  constructor(private apiService: ApiService){}

  get value(): any {
    return this.innerValue;
  }

  set value(v: any) {
    if (v !== this.innerValue) {
      this.innerValue = v;
      this.onChangeCallback(v);
    }
  }

  onBlur() {
    this.onTouchedCallback();
  }

  writeValue(value: any) {
    if (value !== this.innerValue) {
      this.innerValue = value;
    }
  }

  registerOnChange(fn: any) {
    this.onChangeCallback = fn;
  }

  registerOnTouched(fn: any) {
    this.onTouchedCallback = fn;
  }

  ngOnInit() {
    this.apiService.getLista(this.entidad, {})
      .subscribe(
        (data: any[]) => this.lista = data,
        (error) => console.log(error)
      );
  }
}
