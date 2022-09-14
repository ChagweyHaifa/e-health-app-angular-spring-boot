import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MedicalMagazineComponent } from './medical-magazine.component';

describe('MedicalMagazineComponent', () => {
  let component: MedicalMagazineComponent;
  let fixture: ComponentFixture<MedicalMagazineComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MedicalMagazineComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MedicalMagazineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
