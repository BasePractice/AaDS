package ru.mifi.practice.vol8.process;

import lombok.EqualsAndHashCode;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public interface Duplicate {
    int MINIMUM_LINES = 50;
    int MINIMUM_RELATIONS = 2;
    //    String DIRECTORY = "/Users/pastor/github/algorithms-and-data-structures-2024";
    String FILE_SEPARATOR = FileSystems.getDefault().getSeparator();
    //    String REGEXP_SEPARATOR = "/";
    String REGEXP_SEPARATOR = "\\\\";
    String DIRECTORY = "E:\\GitHub\\algorithms-and-data-structures-2024";
    String DIRECTORY_STUDENTS = DIRECTORY + FILE_SEPARATOR + "students" + FILE_SEPARATOR;

    static void main(String[] args) throws XMLStreamException, IOException {
        Default duplicate = new Default();
        List<CodeSegment> parsed = duplicate.parse(DIRECTORY + "/target/cpd.xml");
        Map<String, Student> studentMap = new HashMap<>();
        parsed.forEach(cs -> cs.process(studentMap));
        var students = studentMap.values().stream().sorted().toList();
        try (Writer writer = new FileWriter("duplicate.csv")) {
            writer.append("Source,Target,Value").append("\n");
            for (Student student : students) {
                for (Map.Entry<Student, Student.Relation> entry : student.relations.entrySet()) {
                    Student k = entry.getKey();
                    Student.Relation v = entry.getValue();
                    writer.append(student.code).append(",").append(k.code).append(",")
                        .append(String.valueOf(v.codeFiles.size())).append("\n");
                }
            }
        }
    }

    List<CodeSegment> parse(String fileName) throws XMLStreamException, IOException;

    @EqualsAndHashCode(of = "code")
    final class Student implements Comparable<Student> {
        private final Map<Student, Relation> relations = new HashMap<>();
        private final String code;

        private Student(String code) {
            this.code = code;
        }

        public void add(Student student, CodeFile cf) {
            if (student.code.equals(code) || cf.lines < MINIMUM_LINES || cf.path.endsWith("Student.java")) {
                return;
            }
            Relation relation = relations.computeIfAbsent(student, s -> new Relation());
            relation.codeFiles.add(cf);
        }

        @Override
        public String toString() {
            return code;
        }

        @Override
        public int compareTo(Student o) {
            return Integer.compare(o.relations.size(), relations.size());
        }

        private static final class Relation {
            private final Set<CodeFile> codeFiles = new HashSet<>();

            @Override
            public String toString() {
                return String.valueOf(codeFiles.size());
            }
        }
    }

    record CodeSegment(int lines, int tokens, List<CodeFile> files) {
        CodeSegment(int lines, int tokens) {
            this(lines, tokens, new ArrayList<>());
        }

        void process(Map<String, Student> students) {
            Map<Student, CodeFile> map = new HashMap<>();
            for (CodeFile codeFile : files) {
                String code = codeFile.code();
                map.put(students.computeIfAbsent(code, Student::new), codeFile);
            }
            for (Student student : map.keySet()) {
                for (Student relation : map.keySet()) {
                    if (student.equals(relation)) {
                        continue;
                    }
                    student.add(relation, map.get(relation));
                }
            }
        }
    }

    record CodeFile(String path, int line, int endLine, int lines) {

        CodeFile(String path, int line, int endLine) {
            this(path, line, endLine, endLine - line);
        }

        String code() {
            return path.split(REGEXP_SEPARATOR)[0];
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            CodeFile codeFile = (CodeFile) o;
            return Objects.equals(path, codeFile.path);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(path);
        }

        @Override
        public String toString() {
            String[] parts = path.split(REGEXP_SEPARATOR);
            return lines + ":" + parts[parts.length - 1];
        }
    }

    final class Default implements Duplicate {
        private static final XMLInputFactory XML_INPUT_FACTORY = XMLInputFactory.newInstance();

        private static String readString(StartElement element, String name) {
            return element.getAttributeByName(new QName(name)).getValue();
        }

        private static int readInt(StartElement element, String name) throws XMLStreamException {
            return Integer.parseInt(readString(element, name));
        }

        public static String readValue(XMLEventReader reader) throws XMLStreamException {
            if (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isCharacters()) {
                    return event.asCharacters().getData().trim();
                }
            }
            return null;
        }

        public static String readValue(XMLEventReader reader, String stopTag) throws XMLStreamException {
            StringBuilder builder = new StringBuilder();
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isCharacters()) {
                    builder.append(event.asCharacters().getData().trim());
                } else if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(stopTag)) {
                    break;
                }
            }
            return builder.toString();
        }

        public static void skip(String name, XMLEventReader reader) throws XMLStreamException {
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    skip(startElement.getName().getLocalPart(), reader);
                }
                if (event.isEndElement()) {
                    EndElement endElement = event.asEndElement();
                    if (name.equals(endElement.getName().getLocalPart())) {
                        break;
                    }
                }
            }
        }

        @Override
        public List<CodeSegment> parse(String fileName) throws XMLStreamException, IOException {
            List<CodeSegment> codeSegments = new ArrayList<>();
            try (InputStream stream = new FileInputStream(fileName)) {
                XMLEventReader reader = XML_INPUT_FACTORY.createXMLEventReader(stream);
                while (reader.hasNext()) {
                    XMLEvent event = reader.nextEvent();
                    if (event.isStartElement()) {
                        StartElement element = event.asStartElement();
                        String localPart = element.getName().getLocalPart();
                        if ("pmd-cpd".equals(localPart)) {
                            continue;
                        } else if ("duplication".equals(localPart)) {
                            Attribute lines = element.getAttributeByName(new QName("lines"));
                            Attribute tokens = element.getAttributeByName(new QName("tokens"));
                            CodeSegment segment = new CodeSegment(Integer.parseInt(lines.getValue()), Integer.parseInt(tokens.getValue()));
                            parseDuplicate(segment, reader);
                            codeSegments.add(segment);
                        } else {
                            skip(localPart, reader);
                        }
                    }
                }
            }
            return codeSegments;
        }

        private void parseDuplicate(CodeSegment segment, XMLEventReader reader) throws XMLStreamException {
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                if (event.isStartElement()) {
                    StartElement element = event.asStartElement();
                    String localPart = element.getName().getLocalPart();
                    if ("file".equals(localPart)) {
                        CodeFile file = new CodeFile(
                            readString(element, "path").replace(DIRECTORY_STUDENTS, ""),
                            readInt(element, "line"),
                            readInt(element, "endline")
                        );
                        segment.files.add(file);
                    } else {
                        skip(localPart, reader);
                    }
                } else if (event.isEndElement()) {
                    EndElement element = event.asEndElement();
                    String localPart = element.getName().getLocalPart();
                    if ("duplication".equals(localPart)) {
                        return;
                    }
                }
            }
        }
    }
}
