from sqlalchemy import Column, Integer, String, Date, Enum
from app.database import Base
import enum

class LevelEnum(str, enum.Enum):
    O_LEVEL = "O'level"
    A_LEVEL = "A'level"

class Student(Base):
    __tablename__ = "students"

    id = Column(Integer, primary_key=True, index=True)
    first_name = Column(String, index=True)
    last_name = Column(String, index=True)
    date_of_birth = Column(Date)
    level = Column(Enum(LevelEnum))
    student_number = Column(String, unique=True, index=True)
