from fastapi import APIRouter, Depends, HTTPException, Request, Form
from sqlalchemy.orm import Session
from typing import List
from app import models, schemas
from app.database import SessionLocal, engine
from fastapi.encoders import jsonable_encoder
from fastapi.responses import JSONResponse, RedirectResponse
from fastapi.templating import Jinja2Templates

models.Base.metadata.create_all(bind=engine)

router = APIRouter()
templates = Jinja2Templates(directory="app/templates")

# Dependency to get DB session
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

@router.post("/", response_model=schemas.Student)
def create_student(student: schemas.StudentCreate, db: Session = Depends(get_db)):
    db_student = db.query(models.Student).filter(models.Student.student_number == student.student_number).first()
    if db_student:
        raise HTTPException(status_code=400, detail="Student number already registered")
    new_student = models.Student(**student.dict())
    db.add(new_student)
    db.commit()
    db.refresh(new_student)
    return new_student

@router.get("/", response_model=List[schemas.Student])
def read_students(skip: int = 0, limit: int = 100, db: Session = Depends(get_db)):
    students = db.query(models.Student).offset(skip).limit(limit).all()
    return students

@router.get("/{student_id}", response_model=schemas.Student)
def read_student(student_id: int, db: Session = Depends(get_db)):
    student = db.query(models.Student).filter(models.Student.id == student_id).first()
    if student is None:
        raise HTTPException(status_code=404, detail="Student not found")
    return student

@router.put("/{student_id}", response_model=schemas.Student)
def update_student(student_id: int, student_update: schemas.StudentCreate, db: Session = Depends(get_db)):
    student = db.query(models.Student).filter(models.Student.id == student_id).first()
    if student is None:
        raise HTTPException(status_code=404, detail="Student not found")
    for var, value in vars(student_update).items():
        setattr(student, var, value) if value else None
    db.commit()
    db.refresh(student)
    return student

@router.delete("/{student_id}")
def delete_student(student_id: int, db: Session = Depends(get_db)):
    student = db.query(models.Student).filter(models.Student.id == student_id).first()
    if student is None:
        raise HTTPException(status_code=404, detail="Student not found")
    db.delete(student)
    db.commit()
    return JSONResponse(content={"detail": "Student deleted"})

@router.get("/html")
def list_students_html(request: Request, db: Session = Depends(get_db)):
    students = db.query(models.Student).all()
    return templates.TemplateResponse("student_list.html", {"request": request, "students": students})

@router.get("/new")
def new_student_form(request: Request):
    return templates.TemplateResponse("student_form.html", {"request": request})

@router.post("/create")
def create_student_form(
    first_name: str = Form(...),
    last_name: str = Form(...),
    date_of_birth: str = Form(...),
    level: str = Form(...),
    student_number: str = Form(...),
    db: Session = Depends(get_db)
):
    from datetime import datetime
    dob = datetime.strptime(date_of_birth, "%Y-%m-%d").date()
    student_data = schemas.StudentCreate(
        first_name=first_name,
        last_name=last_name,
        date_of_birth=dob,
        level=level,
        student_number=student_number
    )
    create_student(student_data, db)
    return RedirectResponse(url="/students/html", status_code=303)
