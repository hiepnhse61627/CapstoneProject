<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="yes"/>
    <xsl:template match="/studentInformation">
        <!--<xsl:apply-templates/>-->
        <!--<xsl:value-of select="/"/>-->
        <style>
            custom-body {
            font-family:'Source Sans Pro','Helvetica Neue',Helvetica,Arial,sans-serif;
            }
            table {
            border-collapse: collapse;
            width: 95%;
            }

            table.custom-tbl thead th {
            background-color: #3c8dbc;
            color: white;
            border-bottom: none;
            padding: 10px 18px;
            }
            table.custom-tbl, table.custom-tbl th, table.custom-tbl td{
            box-sizing: content-box;
            }

            .txt-center {
            text-align: center;
            }

            table, th, td {
            border: 1px solid black;
            }

            .inline {
            display: inline;
            float: left;
            }

            .txt-left {
            text-align: left;
            }

            .txt-right {
            text-align: right;
            }
            /* ------------- Width -------------*/
            .width-5 {
            width: 5% !important;
            }

            .width-10 {
            width: 10% !important;
            }

            .width-20 {
            width: 20% !important;
            }

            .width-29 {
            width: 29% !important;
            }

            .width-30 {
            width: 30% !important;
            }

            .width-40 {
            width: 40% !important;
            }

            .width-50 {
            width: 50% !important;
            }

            .width-60 {
            width: 60% !important;
            }

            .width-70 {
            width: 70% !important;
            }

            .width-80 {
            width: 80% !important;
            }

            .width-90 {
            width: 90% !important;
            }

            .width-100 {
            width: 100% !important;
            }

            @media (max-width: 768px) {
            .width-m-10 {
            width: 10% !important;
            }

            .width-m-20 {
            width: 20% !important;
            }

            .width-m-30 {
            width: 30% !important;
            }

            .width-m-40 {
            width: 40% !important;
            }

            .width-m-50 {
            width: 50% !important;
            }

            .width-60 {
            width: 60% !important;
            }

            .width-m-70 {
            width: 70% !important;
            }

            .width-m-80 {
            width: 80% !important;
            }

            .width-m-90 {
            width: 90% !important;
            }

            .width-m-100 {
            width: 100% !important;
            }
            }
        </style>

        <section class="content custom-body"
                 style="font-family:'Source Sans Pro','Helvetica Neue',Helvetica,Arial,sans-serif;">
            <div class="box">
                <div>
                    <div>Chào em,
                    </div>
                    <br/>
                    <div>Phòng Tổ chức và Quản lý đào tạo xin thông báo: em có tên trong Danh sách xét tốt nghiệp
                        <span style="color:#1b75a9;font-weight: bold;">
                            <xsl:value-of select="./graduateTime"/>
                        </span>
                        , cụ thể như sau:
                    </div>
                    <br/>
                    <table style="width: 90%;border-collapse: collapse;border: 1px;">
                        <tbody>
                            <tr style="padding:4px 5px;text-align:center;border: 1px solid #ddd;">
                                <td style="border: 1px solid #ddd;">MSSV
                                </td>
                                <td style="border: 1px solid #ddd;">
                                    <xsl:value-of select="./student/rollNumber"/>
                                </td>
                            </tr>
                            <tr style="padding:4px 5px;text-align:center;border: 1px solid #ddd;">
                                <td style="border: 1px solid #ddd;">Họ và tên
                                </td>
                                <td style="border: 1px solid #ddd;">
                                    <xsl:value-of select="./student/fullName"/>
                                </td>
                            </tr>
                            <tr style="padding:4px 5px;text-align:center;border: 1px solid #ddd;">
                                <td style="border: 1px solid #ddd;">Ngày tháng và năm sinh
                                </td>
                                <td style="border: 1px solid #ddd;">
                                    <xsl:variable name="birthDate2" select="./student/dateOfBirth"/>
                                    <xsl:value-of select="concat(
                      substring($birthDate2, 9, 2),
                      '/',
                      substring($birthDate2, 6, 2),
                      '/',
                      substring($birthDate2, 1, 4)
                      )"/>
                                </td>
                            </tr>
                            <tr style="padding:4px 5px;text-align:center;border: 1px solid #ddd;">
                                <td style="border: 1px solid #ddd;">Giới tính
                                </td>
                                <td style="border: 1px solid #ddd;">
                                    <xsl:variable name="mGender" select="./student/gender"/>
                                    <xsl:choose>
                                        <xsl:when test="$mGender = 'true'">
                                            Nam
                                        </xsl:when>
                                        <xsl:when test="$mGender = 'True'">
                                            Nam
                                        </xsl:when>
                                        <xsl:otherwise>
                                            Nữ
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </td>
                            </tr>
                            <tr style="padding:4px 5px;text-align:center;border: 1px solid #ddd;">
                                <td style="border: 1px solid #ddd;">Quốc tịch
                                </td>
                                <td style="border: 1px solid #ddd;">Việt Nam
                                </td>
                            </tr>
                            <tr style="padding:4px 5px;text-align:center;border: 1px solid #ddd;">
                                <td style="border: 1px solid #ddd;">Ngành đào tạo
                                </td>
                                <td style="border: 1px solid #ddd;">
                                    <xsl:value-of select="./student/programId/fullName"/>
                                </td>
                            </tr>
                            <tr style="padding:4px 5px;text-align:center;border: 1px solid #ddd;">
                                <td style="border: 1px solid #ddd;">Điểm trung bình
                                </td>
                                <td style="border: 1px solid #ddd;">
                                    <xsl:value-of select="./average"/>
                                </td>
                            </tr>
                            <tr style="padding:4px 5px;text-align:center;border: 1px solid #ddd;">
                                <td style="border: 1px solid #ddd;">Xếp loại
                                </td>
                                <td style="border: 1px solid #ddd;">
                                    <xsl:variable name="avgMark" select="./average"/>
                                    <xsl:choose>
                                        <xsl:when test="$avgMark >= 9">
                                            Xuất sắc
                                        </xsl:when>
                                        <xsl:when test="$avgMark >= 8">
                                            Giỏi
                                        </xsl:when>
                                        <xsl:when test="$avgMark >= 7">
                                            Khá
                                        </xsl:when>
                                        <xsl:when test="$avgMark >= 6">
                                            Trung bình khá
                                        </xsl:when>
                                        <xsl:otherwise>
                                            Trung bình
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                    <br/>
                    <div>
                        Các giấy tờ cần thiết khi xét Tốt nghiệp (bản sao/photo công chứng):
                    </div>
                    <div>
                        <table style="width: 90%;border-collapse: collapse;border: 1px;">
                            <thead>
                                <tr style="padding:4px 5px;text-align:center">
                                    <th style="padding-top: 12px;padding-bottom: 12px;
                                    background-color: #4c86af;color: white;">
                                        Bằng TN THPT
                                    </th>
                                    <th style="padding-top: 12px;padding-bottom: 12px;
                                    background-color: #4c86af;color: white;" >
                                        CMND
                                    </th>
                                    <th style="padding-top: 12px;padding-bottom: 12px;
                                    background-color: #4c86af;color: white;">
                                        Giấy khai sinh
                                    </th>
                                </tr>
                            </thead>
                            <tbody>

                                <tr >
                                    <td  style="padding:6px 8px;text-align:center;border: 1px solid #ddd;">
                                        <xsl:variable name="mHighschoolGraduate" select="./highschoolGraduate"/>
                                        <xsl:choose>
                                            <xsl:when test="$mHighschoolGraduate = 'true'">
                                                &#x2713;
                                            </xsl:when>
                                            <xsl:when test="$mHighschoolGraduate = 'True'">
                                                &#x2713;
                                            </xsl:when>
                                            <xsl:otherwise></xsl:otherwise>
                                        </xsl:choose>
                                    </td>
                                    <td  style="padding:6px 8px;text-align:center;border: 1px solid #ddd;">
                                        <xsl:variable name="mIdCard" select="./idCard"/>
                                        <xsl:choose>
                                            <xsl:when test="$mIdCard = 'true'">
                                                &#x2713;
                                            </xsl:when>
                                            <xsl:when test="$mIdCard = 'True'">
                                                &#x2713;
                                            </xsl:when>
                                            <xsl:otherwise></xsl:otherwise>
                                        </xsl:choose>
                                    </td>
                                    <td  style="padding:6px 8px;text-align:center;border: 1px solid #ddd;">
                                        <xsl:variable name="mBirthRecords" select="./birthRecords"/>
                                        <xsl:choose>
                                            <xsl:when test="$mBirthRecords = 'true'">
                                                &#x2713;
                                            </xsl:when>
                                            <xsl:when test="$mBirthRecords = 'True'">
                                                &#x2713;
                                            </xsl:when>
                                            <xsl:otherwise></xsl:otherwise>
                                        </xsl:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                    <br/>
                    <div>
                        Xác nhận của sinh viên về điểm và thông tin cá nhân:
                    </div>
                    <div>
                        Sau khi kiểm tra thông tin cá nhân, điểm và tên đề tài sinh viên trả lời mail này với nội dung:
                    </div>
                    <div>
                        <i style="color:red;">Em đã kiểm tra và xác nhận toàn bộ thông tin về bảng điểm quá trình và
                            thông tin cá nhân là chính xác.
                        </i>
                    </div>
                    <div style=" text-decoration: underline;">
                        <b>Lưu ý:</b>
                    </div>
                    <div>
                        Sinh viên cần hoàn tất mọi thủ tục trên trước
                        <span style="color:#1b75a9;font-weight: bold;">
                            <xsl:value-of select="./dueDate"/>
                        </span>
                        .
                        <span style="color:red;">Nếu quá thời hạn trên, nhà trường sẽ chuyển em sang danh sách xét tốt
                            nghiệp đợt sau.
                        </span>
                    </div>
                    <br/>
                    <div>
                        Thân mến
                    </div>
                </div>

                <div>
                    <div style="width:100%; text-align:center;">
                        <img src="http://fpt.edu.vn/Content/images/assets/Logo-FU-03.png" width="350px"/>
                    </div>
                    <h1 style="width:100%;text-align:center">BẢNG ĐIỂM HỌC TẬP
                        <br/>
                        ACADEMIC TRANSCRIPT
                    </h1>
                </div>
                <div style="padding: 8px 10px;">
                    <div class="inline txt-left width-50 width-m-50" style="display: inline;
                    float: left;text-align: left;width: 50%;">
                        Họ và tên:
                        <xsl:value-of select="./student/fullName"/>
                    </div>
                    <div class="inline txt-left width-50 width-m-50" style="display: inline;
                    float: left;text-align: left;width: 50%;">
                        MSSV:
                        <xsl:value-of select="./student/rollNumber"/>
                    </div>
                </div>
                <br/>
                <div style="padding: 8px 10px;">
                    <div class="inline txt-left width-50 width-m-50"
                         style="display: inline;float: left;text-align: left;width: 50%;">
                        Ngày sinh:
                        <xsl:variable name="birthDate" select="./student/dateOfBirth"/>
                        <xsl:value-of select="concat(
                      substring($birthDate, 9, 2),
                      '/',
                      substring($birthDate, 6, 2),
                      '/',
                      substring($birthDate, 1, 4)
                      )"/>
                    </div>
                    <div class="inline txt-left width-50 width-m-50"
                         style="display: inline;float: left;text-align: left;width: 50%;">Hình thức đào tạo: Chính quy
                    </div>
                </div>
                <br/>
                <div style="padding: 8px 10px;">
                    <div class="inline txt-left width-50 width-m-50"
                         style="display: inline;float: left;text-align: left;width: 50%;">
                        Ngành:
                        <xsl:value-of select="./student/programId/fullName"/>
                    </div>
                    <div class="inline txt-left width-50 width-m-50"
                         style="display: inline;float: left;text-align: left;width: 50%;">
                        Chuyên ngành:
                        <xsl:value-of select="./student/programId/fullName"/>
                    </div>
                </div>
                <br/>
                <table style="border:1px; border-collapse: collapse;width: 95%;" class="custom-tbl">
                    <thead style="background-color: #3c8dbc;color: white;border-bottom: none;padding: 10px 18px;">
                        <th class="txt-center" style="text-align: center;background-color: #3c8dbc;
                        color: white;border-bottom: none;padding: 10px 18px;">No.
                        </th>
                        <th class="txt-center" style="text-align: center;background-color: #3c8dbc;
                        color: white;border-bottom: none;padding: 10px 18px;">Subject
                        </th>
                        <th class="txt-center" style="text-align: center;background-color: #3c8dbc;
                        color: white;border-bottom: none;padding: 10px 18px;">Môn học
                        </th>
                        <th class="txt-center" style="text-align: center;background-color: #3c8dbc;
                        color: white;border-bottom: none;padding: 10px 18px;">Tín chỉ
                        </th>
                        <th class="txt-center" style="text-align: center;background-color: #3c8dbc;
                        color: white;border-bottom: none;padding: 10px 18px;">Điểm số
                        </th>
                        <th class="txt-center" style="text-align: center;background-color: #3c8dbc;
                        color: white;border-bottom: none;padding: 10px 18px;">Điểm chữ
                        </th>
                    </thead>
                    <tbody>
                        <xsl:for-each select="./markDetail">
                            <xsl:element name="tr">
                                <xsl:if test="position() mod 2 = 0">
                                    <xsl:attribute name="style">background-color: #eef1f6;</xsl:attribute>
                                </xsl:if>
                                <xsl:if test="position() mod 2 != 0">
                                    <xsl:attribute name="style">odd</xsl:attribute>
                                </xsl:if>

                                <td class="txt-center"
                                    style="text-align: center;padding:8px 10px;box-sizing: content-box;">
                                    <xsl:value-of select="position()"/>
                                </td>
                                <td class="txt-center"
                                    style="text-align: left;padding:8px 10px;box-sizing: content-box;">
                                    <xsl:value-of select="./mark/subjectMarkComponentId/subjectId/name"/>
                                </td>
                                <td class="txt-center"
                                    style="text-align: left;padding:8px 10px;box-sizing: content-box;">
                                    <xsl:value-of select="./mark/subjectMarkComponentId/subjectId/vnName"/>
                                </td>
                                <td class="txt-center"
                                    style="text-align: center;padding:8px 10px;box-sizing: content-box;">
                                    <xsl:value-of select="./credit"/>
                                </td>
                                <td class="txt-center"
                                    style="text-align: center;padding:8px 10px;box-sizing: content-box;">
                                    <xsl:value-of select="./mark/averageMark"/>
                                </td>
                                <td class="txt-center"
                                    style="text-align: left;padding:8px 10px;box-sizing: content-box;">
                                    <xsl:variable name="mark" select="./mark/averageMark"/>
                                    <xsl:choose>
                                        <xsl:when test="$mark >= 9">
                                            A+
                                        </xsl:when>
                                        <xsl:when test="$mark >= 8.5">
                                            A
                                        </xsl:when>
                                        <xsl:when test="$mark >= 8">
                                            A-
                                        </xsl:when>
                                        <xsl:when test="$mark >= 7.5">
                                            B+
                                        </xsl:when>
                                        <xsl:when test="$mark >= 7">
                                            B
                                        </xsl:when>
                                        <xsl:when test="$mark >= 6.5">
                                            B-
                                        </xsl:when>
                                        <xsl:when test="$mark >= 6">
                                            C+
                                        </xsl:when>
                                        <xsl:when test="$mark >= 5.5">
                                            C
                                        </xsl:when>
                                        <xsl:when test="$mark >= 5">
                                            C-
                                        </xsl:when>
                                        <xsl:when test="$mark = 0">
                                            A+
                                        </xsl:when>
                                        <xsl:otherwise>
                                            F
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </td>
                            </xsl:element>
                        </xsl:for-each>
                    </tbody>

                </table>

                <div style="margin-top:30px">
                    <div style="width:100%;margin-bottom:20px">Tên đồ án:
                        <xsl:value-of select="./vnThesisName"/>
                    </div>
                    <br/>
                    <div style="width:100%;margin-bottom:20px">Capstone Project:
                        <xsl:value-of select="./engThesisName"/>
                    </div>
                </div>

                <div style="margin: 30px 0px;">
                    <div class="inline txt-left width-50 width-m-50" style="display: inline;
                    float: left;text-align: left;width: 50%;">
                        <span style="">Xếp hạng tốt nghiệp:
                            <xsl:variable name="avgMark" select="./average"/>
                            <xsl:choose>
                                <xsl:when test="$avgMark >= 9">
                                    Xuất sắc
                                </xsl:when>
                                <xsl:when test="$avgMark >= 8">
                                    Giỏi
                                </xsl:when>
                                <xsl:when test="$avgMark >= 7">
                                    Khá
                                </xsl:when>
                                <xsl:when test="$avgMark >= 6">
                                    Trung bình khá
                                </xsl:when>
                                <xsl:otherwise>
                                    Trung bình
                                </xsl:otherwise>
                            </xsl:choose>
                        </span>
                    </div>
                    <div class="inline txt-left width-50 width-m-50" style="display: inline;
                    float: left;text-align: left;width: 50%;">
                        <!--5 spacing-->
                        <span style="">Điểm trung bình chung học tập:
                            <xsl:value-of select="./average"/>
                        </span>

                    </div>
                </div>

            </div>
        </section>
    </xsl:template>
    <!--<xsl:template match="/student">-->
    <!--<h4>-->
    <!--<xsl:value-of select="rollNumber"/>-->
    <!--</h4>-->
    <!--<p>-->
    <!--<xsl:value-of select="fullName"/>-->
    <!--</p>-->
    <!--<p>-->
    <!--Ngành:-->
    <!--<xsl:value-of select="programId/fullName"/>-->
    <!--</p>-->
    <!--</xsl:template>-->
</xsl:stylesheet>