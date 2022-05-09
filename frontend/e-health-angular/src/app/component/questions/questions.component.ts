import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, NgForm, Validators } from '@angular/forms';
import { Subscription } from 'rxjs';
import { NotificationType } from 'src/app/enum/notification-type.enum';
import { CustomHttpResponse } from 'src/app/model/custom-http-response';
import { Question } from 'src/app/model/question';
import { QuestionResponse } from 'src/app/model/question-response';
import { Speciality } from 'src/app/model/speciality';
import { FormService } from 'src/app/service/form.service';
import { NotificationService } from 'src/app/service/notification.service';
import { QuestionService } from 'src/app/service/question.service';

@Component({
  selector: 'app-questions',
  templateUrl: './questions.component.html',
  styleUrls: ['./questions.component.css'],
})
export class QuestionsComponent implements OnInit {
  private subscriptions: Subscription[] = [];
  specialities: Speciality[];
  showLoading: boolean = false;

  questions: Question[];
  myQuestionForm: FormGroup;
  editMyQuestionForm: FormGroup;

  currenSpecialityName: string;
  files: File[] = [];
  respondToQuestionForm: FormGroup;

  questionDetailsToAdd: string;
  questionTitleToAdd: string;
  isEditQuestionResponse: boolean;
  isAddQuestionResponse: boolean;

  constructor(
    private formService: FormService,
    private notificationService: NotificationService,
    private formBuilder: FormBuilder,
    private questionService: QuestionService
  ) {
    this.myQuestionForm = this.formBuilder.group({
      speciality: ['', Validators.required],
      questionTitle: ['', Validators.required],
      question: ['', Validators.required],
      currentTreatment: [''],
      medicalHistory: [''],
      weight: [''],
      height: [''],
      files: '',
    });

    this.editMyQuestionForm = this.formBuilder.group({
      questionId: [Validators.required],
      speciality: [Validators.required],
      questionTitle: ['', Validators.required],
      question: ['', Validators.required],
      currentTreatment: [''],
      medicalHistory: [''],
      weight: [''],
      height: [''],
      files: '',
    });

    this.respondToQuestionForm = this.formBuilder.group({
      questionId: '',
      questionResponse: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.getSpecialities();
    this.getQuestionsBySpeciality('Cardiologue');
  }

  // onAddAQuestion() {
  //   this.defaultSpeciality = this.specialities[1];
  //   this.myQuestionForm.patchValue({
  //     speciality: this.defaultSpeciality,
  //   });
  // }
  getSpecialities() {
    this.subscriptions.push(
      this.formService.getSpecialities().subscribe(
        (response: Speciality[]) => {
          this.specialities = response;
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      )
    );
  }
  getQuestionsBySpeciality(specialityName: string) {
    this.currenSpecialityName = specialityName;
    this.questionService.getQuestionsBySpeciality(specialityName).subscribe(
      (response: Question[]) => {
        this.questions = response;
      },

      (errorResponse: HttpErrorResponse) => {
        this.sendNotification(
          NotificationType.ERROR,
          errorResponse.error.message
        );
      }
    );
  }

  showResponse(question: Question) {}

  // add question
  uploadMyQuestionFormFiles(event: Event) {
    const lengh: number = (<HTMLInputElement>event.target).files.length;
    for (var i = 0; i < lengh; i++) {
      this.files.push((<HTMLInputElement>event.target).files[i]);
    }
  }

  addQuestion() {
    // console.log(this.myQuestionForm.value);
    this.showLoading = true;
    const formData = new FormData();
    formData.append('specialityName', this.myQuestionForm.value.speciality);
    formData.append('questionTitle', this.myQuestionForm.value.questionTitle);
    formData.append('question', this.myQuestionForm.value.question);
    formData.append(
      'currentTreatment',
      this.myQuestionForm.value.currentTreatment
    );
    formData.append('medicalHistory', this.myQuestionForm.value.medicalHistory);
    formData.append('weight', this.myQuestionForm.value.weight);
    formData.append('height', this.myQuestionForm.value.height);
    for (const file of this.files) {
      formData.append('attachements', file);
    }
    this.questionService.addQuestion(formData).subscribe(
      (response: Question) => {
        this.getQuestionsBySpeciality(this.myQuestionForm.value.speciality);
        this.showLoading = false;
        this.clickButton('my-question-form-close-btn');
        this.myQuestionForm.reset();
        this.sendNotification(
          NotificationType.SUCCESS,
          'You have added your question successfully'
        );
      },

      (errorResponse: HttpErrorResponse) => {
        this.showLoading = false;
        this.sendNotification(
          NotificationType.ERROR,
          errorResponse.error.message
        );
      }
    );
  }

  // edit question
  launchEditQuestionModal(selectedQuestion: Question) {
    this.clickButton('edit-question-modal-trigger-btn');
    this.editMyQuestionForm.patchValue({
      questionId: selectedQuestion.id,
      speciality: selectedQuestion.speciality.name,
      questionTitle: selectedQuestion.title,
      question: selectedQuestion.question,
      currentTreatment: selectedQuestion.currentTreatment,
      medicalHistory: selectedQuestion.medicalHistory,
      weight: selectedQuestion.questionerWeight,
      height: selectedQuestion.questionerHeight,
      files: '',
    });
  }

  editMyQuestion() {
    // console.log(this.editMyQuestionForm.value);
    this.showLoading = true;
    const formData = new FormData();
    formData.append('questionId', this.editMyQuestionForm.value.questionId);
    formData.append('specialityName', this.editMyQuestionForm.value.speciality);
    formData.append(
      'questionTitle',
      this.editMyQuestionForm.value.questionTitle
    );
    formData.append('question', this.editMyQuestionForm.value.question);
    formData.append(
      'currentTreatment',
      this.editMyQuestionForm.value.currentTreatment
    );
    formData.append(
      'medicalHistory',
      this.editMyQuestionForm.value.medicalHistory
    );
    formData.append('weight', this.editMyQuestionForm.value.weight);
    formData.append('height', this.editMyQuestionForm.value.height);
    for (const file of this.files) {
      formData.append('attachements', file);
    }
    this.questionService.editQuestion(formData).subscribe(
      (response: Question) => {
        this.getQuestionsBySpeciality(this.currenSpecialityName);
        this.showLoading = false;
        this.clickButton('edit-my-question-form-close-btn');
        this.sendNotification(
          NotificationType.SUCCESS,
          'You have edited your question successfully'
        );
      },

      (errorResponse: HttpErrorResponse) => {
        this.showLoading = false;
        this.sendNotification(
          NotificationType.ERROR,
          errorResponse.error.message
        );
      }
    );
  }
  deleteQuestion(questionId: bigint) {
    // console.log(this.editMyQuestionForm.value.questionId);
    const isConfirmed = confirm('Are you sure to delete this question ?');
    if (isConfirmed) {
      this.questionService.deleteQuestion(questionId).subscribe(
        (response: CustomHttpResponse) => {
          this.getQuestionsBySpeciality(this.currenSpecialityName);
          this.sendNotification(NotificationType.SUCCESS, response.message);
        },

        (errorResponse: HttpErrorResponse) => {
          this.showLoading = false;
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      );
    }
  }

  launchRespondToQuestionModal(question: Question) {
    this.isEditQuestionResponse = false;
    this.isAddQuestionResponse = true;
    this.respondToQuestionForm.patchValue({
      questionId: question.id,
    });
    this.clickButton('add-response-modal-trigger-btn');
    this.questionTitleToAdd = `${question.title}`;
    this.questionDetailsToAdd = `<div>
                          ${question.question}
                          </div>`;
  }

  respondToQuestion() {
    let questionResponse = new QuestionResponse();
    questionResponse.content =
      this.respondToQuestionForm.value.questionResponse;
    this.questionService
      .addResponse(
        this.respondToQuestionForm.value.questionId,
        questionResponse
      )
      .subscribe(
        (response: Question) => {
          this.getQuestionsBySpeciality(this.currenSpecialityName);
          this.clickButton('respond-to-question-modal-close-btn');
          this.respondToQuestionForm.reset();
          this.sendNotification(
            NotificationType.SUCCESS,
            'you have responded to the question successfully '
          );
        },

        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      );
  }

  onEditQuestionResponse(question: Question) {
    this.isEditQuestionResponse = true;
    this.isAddQuestionResponse = false;
    this.respondToQuestionForm.patchValue({
      questionId: question.id,
      questionResponse: question.response.content,
    });
    this.clickButton('add-response-modal-trigger-btn');
    this.questionTitleToAdd = `${question.title}`;
    this.questionDetailsToAdd = `<div>
                          ${question.question}
                          </div>`;
  }
  editQuestionResponse() {
    let questionResponse = new QuestionResponse();
    questionResponse.content =
      this.respondToQuestionForm.value.questionResponse;
    console.log(questionResponse);
    console.log(this.respondToQuestionForm.value.questionId);
    this.questionService
      .editQuestionResponse(
        this.respondToQuestionForm.value.questionId,
        questionResponse
      )
      .subscribe(
        (response: Question) => {
          this.getQuestionsBySpeciality(this.currenSpecialityName);
          this.clickButton('respond-to-question-modal-close-btn');
          this.sendNotification(
            NotificationType.SUCCESS,
            'you have edited your response to the question successfully'
          );
        },

        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      );
  }

  deleteQuestionResponse(questionId: bigint) {
    const isConfirmed = confirm(
      'Are you sure to delete your response to this question ?'
    );
    if (isConfirmed) {
      this.questionService.deleteQuestionResponse(questionId).subscribe(
        (response: Question) => {
          this.getQuestionsBySpeciality(this.currenSpecialityName);
          this.sendNotification(
            NotificationType.SUCCESS,
            'you have deleted your response to this question successfully'
          );
        },

        (errorResponse: HttpErrorResponse) => {
          this.showLoading = false;
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
        }
      );
    }
  }

  private sendNotification(
    notificationType: NotificationType,
    message: string
  ): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      // if we didn't get any message from the backend, for example in case we didn't start the backend server
      this.notificationService.notify(
        notificationType,
        'An error occurred. Please try again.'
      );
    }
  }

  private clickButton(buttonId: string) {
    document.getElementById(buttonId).click();
  }
}
